package com.braids.burncoffeeman.common;

import java.awt.Color;
import java.nio.charset.Charset;

public class Constants {

	public static final Color   TRANSPARENT_COLOR_1                     = new Color(96, 96, 96);
	public static final Color   TRANSPARENT_COLOR_2                     = new Color(160, 160, 160);

	public static final Color   COLOR_KEY_OWN_COLOR_1                   = new Color(255, 0, 255, 128);
	public static final Color   COLOR_KEY_OWN_COLOR_2                   = new Color(255, 1, 255, 128);

	public static final int     MAX_ANIM_PHASE_COUNT                    = 4;

	public static final int     MAX_PACKET_SIZE                         = 256 * 1024;

	public static final int     MAIN_CYCLE_PERIOD                       = 40;

	public static final Charset UTF_8                                   = Charset.forName("utf-8");

	public static final int     MAX_UNSENT_BUFFER                       = 128 * 1024;

	public static final int     LEVEL_WIDTH                             = 11;
	public static final int     LEVEL_HEIGHT                            = 11;

	public static final int     COMPONENT_SIZE_IN_VIRTUAL               = 1155;

	public static final int     SEND_MAP_SEGMENT_PACKET_SIZE            = 1;
	public static final int     SEND_MAP_SEGMENT_BOOST_PACKET_SIZE      = 20;

	public static final int     MOVEMENT_CORRECTION_SENSITIVITY         = 85;

	public static final int     LEVEL_COMPONENT_GRANULARITY             = COMPONENT_SIZE_IN_VIRTUAL;

	public static final int     BOMBERMAN_BASIC_SPEED                   = LEVEL_COMPONENT_GRANULARITY / 6;
	public static final int     BOBMERMAN_ROLLER_SKATES_SPEED_INCREMENT = BOMBERMAN_BASIC_SPEED * 15 / 100;
	public static final int     BOBMERMAN_MAX_SPEED                     = BOMBERMAN_BASIC_SPEED * 3;

	public static final int     MAX_BOMB_ID                             = 256 * 256 - 2;

	public static final int     BOMB_FLYING_DISTANCE                    = LEVEL_COMPONENT_GRANULARITY * 3;
}
