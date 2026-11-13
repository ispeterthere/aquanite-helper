/*
 * Copyright (c) 2026, Pedro
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.aquanitehelper;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.gameval.NpcID;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Aquanite Helper",
	description = "Reminds you to slash aquanites until their lure is cut",
	tags = {"aquanite", "slayer", "lure", "slash", "stab"}
)
public class AquaniteHelperPlugin extends Plugin
{
	// Aquanites that still have their lure: regular aquanite and its superior, the elder aquanite
	private static final Set<Integer> LURED_AQUANITE_IDS = ImmutableSet.of(
		NpcID.SLAYER_AQUANITE,
		NpcID.SUPERIOR_AQUANITE
	);
	// The same aquanites after their lure has been cut
	private static final Set<Integer> NO_LURE_AQUANITE_IDS = ImmutableSet.of(
		NpcID.SLAYER_AQUANITE_NOLURE,
		NpcID.SUPERIOR_AQUANITE_NOLURE
	);
	private static final String LURE_CUT_MESSAGE = "cuts off the aquanite's lure";
	private static final int CUT_CONFIRM_TICKS = 4;

	@Inject
	private Client client;

	@Inject
	private AquaniteHelperConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AquaniteHelperOverlay overlay;

	@Inject
	private Notifier notifier;

	// NPC indexes whose lure has been cut, in case the NPC keeps its lured ID briefly after the message
	private final Set<Integer> cutNpcIndexes = new HashSet<>();

	@Getter
	private boolean warningActive;

	@Getter
	private int cutConfirmTicks;

	private NPC lastTarget;

	@Provides
	AquaniteHelperConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AquaniteHelperConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		reset();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN || event.getGameState() == GameState.HOPPING)
		{
			reset();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (cutConfirmTicks > 0)
		{
			cutConfirmTicks--;
		}

		NPC target = getLuredTarget();
		if (target != null)
		{
			lastTarget = target;
			if (!warningActive)
			{
				log.debug("Attacking lured aquanite id={} index={}", target.getId(), target.getIndex());
				if (config.notifyOnLure())
				{
					notifier.notify("Aquanite lure is up - use a slash attack!");
				}
			}
		}
		warningActive = target != null;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE && event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String message = Text.removeTags(event.getMessage()).toLowerCase().replace('\u2019', '\'');
		if (!message.contains(LURE_CUT_MESSAGE))
		{
			return;
		}

		NPC target = getCurrentNpcTarget();
		onLureCut(target != null ? target : lastTarget);
	}

	@Subscribe
	public void onNpcChanged(NpcChanged event)
	{
		NPC npc = event.getNpc();
		if (event.getOld() != null
			&& LURED_AQUANITE_IDS.contains(event.getOld().getId())
			&& NO_LURE_AQUANITE_IDS.contains(npc.getId()))
		{
			onLureCut(npc);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		NPC npc = event.getNpc();
		cutNpcIndexes.remove(npc.getIndex());
		if (npc == lastTarget)
		{
			lastTarget = null;
		}
	}

	private void onLureCut(NPC npc)
	{
		if (npc == null || !cutNpcIndexes.add(npc.getIndex()))
		{
			// Unknown NPC, or this lure cut was already handled by the other event
			return;
		}

		log.debug("Lure cut on aquanite index={}", npc.getIndex());

		// Only react for the aquanite we were warning about, not one someone else is fighting
		if (npc != lastTarget)
		{
			return;
		}

		warningActive = false;
		if (config.cutConfirm())
		{
			cutConfirmTicks = CUT_CONFIRM_TICKS;
		}
	}

	private void reset()
	{
		cutNpcIndexes.clear();
		warningActive = false;
		cutConfirmTicks = 0;
		lastTarget = null;
	}

	private NPC getCurrentNpcTarget()
	{
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return null;
		}

		Actor interacting = local.getInteracting();
		return interacting instanceof NPC ? (NPC) interacting : null;
	}

	private NPC getLuredTarget()
	{
		NPC npc = getCurrentNpcTarget();
		if (npc == null || npc.isDead())
		{
			return null;
		}

		if (!LURED_AQUANITE_IDS.contains(npc.getId()) || cutNpcIndexes.contains(npc.getIndex()))
		{
			return null;
		}

		return npc;
	}
}
