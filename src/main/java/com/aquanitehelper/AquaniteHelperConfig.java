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
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(AquaniteHelperConfig.GROUP)
public interface AquaniteHelperConfig extends Config
{
	String GROUP = "aquanitehelper";

	@ConfigItem(
		keyName = "warningText",
		name = "Warning text",
		description = "Text shown in the warning box",
		position = 1
	)
	default String warningText()
	{
		return "SLASH the lure!";
	}

	@Alpha
	@ConfigItem(
		keyName = "warningColor",
		name = "Warning color",
		description = "Background color of the warning box",
		position = 2
	)
	default Color warningColor()
	{
		return new Color(200, 30, 30, 190);
	}

	@ConfigItem(
		keyName = "flash",
		name = "Flash warning",
		description = "Make the warning box flash",
		position = 3
	)
	default boolean flash()
	{
		return true;
	}

	@ConfigItem(
		keyName = "notify",
		name = "System notification",
		description = "Send a RuneLite notification when you start attacking a lured aquanite",
		position = 4
	)
	default boolean notifyOnLure()
	{
		return false;
	}

	@ConfigItem(
		keyName = "cutConfirm",
		name = "Show 'switch to stab' prompt",
		description = "Briefly show a prompt to switch back to stab after the lure is cut",
		position = 5
	)
	default boolean cutConfirm()
	{
		return true;
	}

	@ConfigItem(
		keyName = "cutConfirmText",
		name = "Prompt text",
		description = "Text shown in the 'switch to stab' prompt",
		position = 6
	)
	default String cutConfirmText()
	{
		return "Lure cut - switch to STAB";
	}

	@Alpha
	@ConfigItem(
		keyName = "cutConfirmColor",
		name = "Prompt color",
		description = "Background color of the 'switch to stab' prompt",
		position = 7
	)
	default Color cutConfirmColor()
	{
		return new Color(30, 150, 50, 190);
	}
}
