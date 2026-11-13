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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.TitleComponent;

class AquaniteHelperOverlay extends OverlayPanel
{
	private static final int FLASH_CYCLES = 20;

	private final Client client;
	private final AquaniteHelperPlugin plugin;
	private final AquaniteHelperConfig config;

	@Inject
	private AquaniteHelperOverlay(Client client, AquaniteHelperPlugin plugin, AquaniteHelperConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (plugin.isWarningActive())
		{
			Color background = config.warningColor();
			if (config.flash() && (client.getGameCycle() / FLASH_CYCLES) % 2 == 0)
			{
				background = background.darker().darker();
			}
			return renderBox(graphics, config.warningText(), background);
		}

		if (plugin.getCutConfirmTicks() > 0)
		{
			return renderBox(graphics, config.cutConfirmText(), config.cutConfirmColor());
		}

		return null;
	}

	private Dimension renderBox(Graphics2D graphics, String text, Color background)
	{
		panelComponent.setBackgroundColor(background);
		panelComponent.getChildren().add(TitleComponent.builder()
			.text(text)
			.color(Color.WHITE)
			.build());
		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(text) + 20, 0));
		return super.render(graphics);
	}
}
