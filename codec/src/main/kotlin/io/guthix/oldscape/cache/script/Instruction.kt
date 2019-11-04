/**
 * This file is part of Guthix OldScape.
 *
 * Guthix OldScape is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Guthix OldScape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.guthix.oldscape.cache.script

class SwitchInstruction(opcode: Int, name: String, val operand: Map<Int, Int>): InstructionDefinition(opcode, name)

class StringInstruction(opcode: Int, name: String, val operand: String): InstructionDefinition(opcode, name)

class IntInstruction(opcode: Int, name: String, val operand: Int): InstructionDefinition(opcode, name)

open class InstructionDefinition(val opcode: Int, val name: String) {
    val isJump get() = when (this) {
        JUMP, IF_ICMPEQ, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE, IF_ICMPLT, IF_ICMPNE -> true
        else -> false
    }

    internal companion object {
        val byName = mutableMapOf<String, InstructionDefinition>()
        val byOpcode = mutableMapOf<Int, InstructionDefinition>()

        val ICONST = InstructionDefinition(0, "iconst")
        val GET_VARP = InstructionDefinition(1, "get_varp")
        val SET_VARP = InstructionDefinition(2, "set_varp")
        val SCONST = InstructionDefinition(3, "sconst")
        val JUMP = InstructionDefinition(6, "jump")
        val IF_ICMPNE = InstructionDefinition(7, "if_icmpne")
        val IF_ICMPEQ = InstructionDefinition(8, "if_icmpeq")
        val IF_ICMPLT = InstructionDefinition(9, "if_icmplt")
        val IF_ICMPGT = InstructionDefinition(10, "if_icmpgt")
        val RETURN = InstructionDefinition(21, "return")
        val GET_VARBIT = InstructionDefinition(25, "get_varbit")
        val SET_VARBIT = InstructionDefinition(27, "set_varbit")
        val IF_ICMPLE = InstructionDefinition(31, "if_icmple")
        val IF_ICMPGE = InstructionDefinition(32, "if_icmpge")
        val ILOAD = InstructionDefinition(33, "iload")
        val ISTORE = InstructionDefinition(34, "istore")
        val SLOAD = InstructionDefinition(35, "sload")
        val SSTORE = InstructionDefinition(36, "sstore")
        val JOIN_STRING = InstructionDefinition(37, "join_string")
        val POP_INT = InstructionDefinition(38, "pop_int")
        val POP_STRING = InstructionDefinition(39, "pop_string")
        val INVOKE = InstructionDefinition(40, "invoke")
        val GET_VARC_INT = InstructionDefinition(42, "get_varc_int")
        val SET_VARC_INT = InstructionDefinition(43, "set_varc_int")
        val DEFINE_ARRAY = InstructionDefinition(44, "define_array")
        val GET_ARRAY_INT = InstructionDefinition(45, "get_array_int")
        val SET_ARRAY_INT = InstructionDefinition(46, "set_array_int")
        val GET_VARC_STRING_OLD = InstructionDefinition(47, "get_varc_string_old")
        val SET_VARC_STRING_OLD = InstructionDefinition(48, "set_varc_string_old")
        val GET_VARC_STRING = InstructionDefinition(49, "get_varc_string")
        val SET_VARC_STRING = InstructionDefinition(50, "set_varc_string")
        val SWITCH = InstructionDefinition(60, "switch")
        val CC_CREATE = InstructionDefinition(100, "cc_create")
        val CC_DELETE = InstructionDefinition(101, "cc_delete")
        val CC_DELETEALL = InstructionDefinition(102, "cc_deleteall")
        val CC_FIND = InstructionDefinition(200, "cc_find")
        val IF_FIND = InstructionDefinition(201, "if_find")
        val CC_SETPOSITION = InstructionDefinition(1000, "cc_setposition")
        val CC_SETSIZE = InstructionDefinition(1001, "cc_setsize")
        val CC_SETHIDE = InstructionDefinition(1003, "cc_sethide")
        val CC_SETNOCLICKTHROUGH = InstructionDefinition(1005, "cc_setnoclickthrough")
        val CC_SETSCROLLPOS = InstructionDefinition(1100, "cc_setscrollpos")
        val CC_SETCOLOUR = InstructionDefinition(1101, "cc_setcolour")
        val CC_SETFILL = InstructionDefinition(1102, "cc_setfill")
        val CC_SETTRANS = InstructionDefinition(1103, "cc_settrans")
        val CC_SETLINEWID = InstructionDefinition(1104, "cc_setlinewid")
        val CC_SETGRAPHIC = InstructionDefinition(1105, "cc_setgraphic")
        val CC_SET2DANGLE = InstructionDefinition(1106, "cc_set2dangle")
        val CC_SETTILING = InstructionDefinition(1107, "cc_settiling")
        val CC_SETMODEL = InstructionDefinition(1108, "cc_setmodel")
        val CC_SETMODELANGLE = InstructionDefinition(1109, "cc_setmodelangle")
        val CC_SETMODELANIM = InstructionDefinition(1110, "cc_setmodelanim")
        val CC_SETMODELORTHOG = InstructionDefinition(1111, "cc_setmodelorthog")
        val CC_SETTEXT = InstructionDefinition(1112, "cc_settext")
        val CC_SETTEXTFONT = InstructionDefinition(1113, "cc_settextfont")
        val CC_SETTEXTALIGN = InstructionDefinition(1114, "cc_settextalign")
        val CC_SETTEXTSHADOW = InstructionDefinition(1115, "cc_settextshadow")
        val CC_SETOUTLINE = InstructionDefinition(1116, "cc_setoutline")
        val CC_SETGRAPHICSHADOW = InstructionDefinition(1117, "cc_setgraphicshadow")
        val CC_SETVFLIP = InstructionDefinition(1118, "cc_setvflip")
        val CC_SETHFLIP = InstructionDefinition(1119, "cc_sethflip")
        val CC_SETSCROLLSIZE = InstructionDefinition(1120, "cc_setscrollsize")
        val CC_RESUME_PAUSEBUTTON = InstructionDefinition(1121, "cc_resume_pausebutton")
        val CC_SETFILLCOLOUR = InstructionDefinition(1123, "cc_setfillcolour")
        val CC_SETLINEDIRECTION = InstructionDefinition(1126, "cc_setlinedirection")
        val CC_SETOBJECT = InstructionDefinition(1200, "cc_setobject")
        val CC_SETNPCHEAD = InstructionDefinition(1201, "cc_setnpchead")
        val CC_SETPLAYERHEAD_SELF = InstructionDefinition(1202, "cc_setplayerhead_self")
        val CC_SETOBJECT_NONUM = InstructionDefinition(1205, "cc_setobject_nonum")
        val CC_SETOBJECT_ALWAYS_NUM = InstructionDefinition(1212, "cc_setobject_always_num")
        val CC_SETOP = InstructionDefinition(1300, "cc_setop")
        val CC_SETDRAGGABLE = InstructionDefinition(1301, "cc_setdraggable")
        val CC_SETDRAGGABLEBEHAVIOR = InstructionDefinition(1302, "cc_setdraggablebehavior")
        val CC_SETDRAGDEADZONE = InstructionDefinition(1303, "cc_setdragdeadzone")
        val CC_SETDRAGDEADTIME = InstructionDefinition(1304, "cc_setdragdeadtime")
        val CC_SETOPBASE = InstructionDefinition(1305, "cc_setopbase")
        val CC_SETTARGETVERB = InstructionDefinition(1306, "cc_settargetverb")
        val CC_CLEAROPS = InstructionDefinition(1307, "cc_clearops")
        val CC_SETONCLICK = InstructionDefinition(1400, "cc_setonclick")
        val CC_SETONHOLD = InstructionDefinition(1401, "cc_setonhold")
        val CC_SETONRELEASE = InstructionDefinition(1402, "cc_setonrelease")
        val CC_SETONMOUSEOVER = InstructionDefinition(1403, "cc_setonmouseover")
        val CC_SETONMOUSELEAVE = InstructionDefinition(1404, "cc_setonmouseleave")
        val CC_SETONDRAG = InstructionDefinition(1405, "cc_setondrag")
        val CC_SETONTARGETLEAVE = InstructionDefinition(1406, "cc_setontargetleave")
        val CC_SETONVARTRANSMIT = InstructionDefinition(1407, "cc_setonvartransmit")
        val CC_SETONTIMER = InstructionDefinition(1408, "cc_setontimer")
        val CC_SETONOP = InstructionDefinition(1409, "cc_setonop")
        val CC_SETONDRAGCOMPLETE = InstructionDefinition(1410, "cc_setondragcomplete")
        val CC_SETONCLICKREPEAT = InstructionDefinition(1411, "cc_setonclickrepeat")
        val CC_SETONMOUSEREPEAT = InstructionDefinition(1412, "cc_setonmouserepeat")
        val CC_SETONINVTRANSMIT = InstructionDefinition(1414, "cc_setoninvtransmit")
        val CC_SETONSTATTRANSMIT = InstructionDefinition(1415, "cc_setonstattransmit")
        val CC_SETONTARGETENTER = InstructionDefinition(1416, "cc_setontargetenter")
        val CC_SETONSCROLLWHEEL = InstructionDefinition(1417, "cc_setonscrollwheel")
        val CC_SETONCHATTRANSMIT = InstructionDefinition(1418, "cc_setonchattransmit")
        val CC_SETONKEY = InstructionDefinition(1419, "cc_setonkey")
        val CC_SETONFRIENDTRANSMIT = InstructionDefinition(1420, "cc_setonfriendtransmit")
        val CC_SETONCLANTRANSMIT = InstructionDefinition(1421, "cc_setonclantransmit")
        val CC_SETONMISCTRANSMIT = InstructionDefinition(1422, "cc_setonmisctransmit")
        val CC_SETONDIALOGABORT = InstructionDefinition(1423, "cc_setondialogabort")
        val CC_SETONSUBCHANGE = InstructionDefinition(1424, "cc_setonsubchange")
        val CC_SETONSTOCKTRANSMIT = InstructionDefinition(1425, "cc_setonstocktransmit")
        val CC_SETONRESIZE = InstructionDefinition(1427, "cc_setonresize")
        val CC_GETX = InstructionDefinition(1500, "cc_getx")
        val CC_GETY = InstructionDefinition(1501, "cc_gety")
        val CC_GETWIDTH = InstructionDefinition(1502, "cc_getwidth")
        val CC_GETHEIGHT = InstructionDefinition(1503, "cc_getheight")
        val CC_GETHIDE = InstructionDefinition(1504, "cc_gethide")
        val CC_GETLAYER = InstructionDefinition(1505, "cc_getlayer")
        val CC_GETSCROLLX = InstructionDefinition(1600, "cc_getscrollx")
        val CC_GETSCROLLY = InstructionDefinition(1601, "cc_getscrolly")
        val CC_GETTEXT = InstructionDefinition(1602, "cc_gettext")
        val CC_GETSCROLLWIDTH = InstructionDefinition(1603, "cc_getscrollwidth")
        val CC_GETSCROLLHEIGHT = InstructionDefinition(1604, "cc_getscrollheight")
        val CC_GETMODELZOOM = InstructionDefinition(1605, "cc_getmodelzoom")
        val CC_GETMODELANGLE_X = InstructionDefinition(1606, "cc_getmodelangle_x")
        val CC_GETMODELANGLE_Z = InstructionDefinition(1607, "cc_getmodelangle_z")
        val CC_GETMODELANGLE_Y = InstructionDefinition(1608, "cc_getmodelangle_y")
        val CC_GETTRANS = InstructionDefinition(1609, "cc_gettrans")
        val CC_GETCOLOUR = InstructionDefinition(1611, "cc_getcolour")
        val CC_GETFILLCOLOUR = InstructionDefinition(1612, "cc_getfillcolour")
        val CC_GETINVOBJECT = InstructionDefinition(1700, "cc_getinvobject")
        val CC_GETINVCOUNT = InstructionDefinition(1701, "cc_getinvcount")
        val CC_GETID = InstructionDefinition(1702, "cc_getid")
        val CC_GETTARGETMASK = InstructionDefinition(1800, "cc_gettargetmask")
        val CC_GETOP = InstructionDefinition(1801, "cc_getop")
        val CC_GETOPBASE = InstructionDefinition(1802, "cc_getopbase")
        val CC_CALLONRESIZE = InstructionDefinition(1927, "cc_callonresize")
        val IF_SETPOSITION = InstructionDefinition(2000, "if_setposition")
        val IF_SETSIZE = InstructionDefinition(2001, "if_setsize")
        val IF_SETHIDE = InstructionDefinition(2003, "if_sethide")
        val IF_SETNOCLICKTHROUGH = InstructionDefinition(2005, "if_setnoclickthrough")
        val IF_SETSCROLLPOS = InstructionDefinition(2100, "if_setscrollpos")
        val IF_SETCOLOUR = InstructionDefinition(2101, "if_setcolour")
        val IF_SETFILL = InstructionDefinition(2102, "if_setfill")
        val IF_SETTRANS = InstructionDefinition(2103, "if_settrans")
        val IF_SETLINEWID = InstructionDefinition(2104, "if_setlinewid")
        val IF_SETGRAPHIC = InstructionDefinition(2105, "if_setgraphic")
        val IF_SET2DANGLE = InstructionDefinition(2106, "if_set2dangle")
        val IF_SETTILING = InstructionDefinition(2107, "if_settiling")
        val IF_SETMODEL = InstructionDefinition(2108, "if_setmodel")
        val IF_SETMODELANGLE = InstructionDefinition(2109, "if_setmodelangle")
        val IF_SETMODELANIM = InstructionDefinition(2110, "if_setmodelanim")
        val IF_SETMODELORTHOG = InstructionDefinition(2111, "if_setmodelorthog")
        val IF_SETTEXT = InstructionDefinition(2112, "if_settext")
        val IF_SETTEXTFONT = InstructionDefinition(2113, "if_settextfont")
        val IF_SETTEXTALIGN = InstructionDefinition(2114, "if_settextalign")
        val IF_SETTEXTSHADOW = InstructionDefinition(2115, "if_settextshadow")
        val IF_SETOUTLINE = InstructionDefinition(2116, "if_setoutline")
        val IF_SETGRAPHICSHADOW = InstructionDefinition(2117, "if_setgraphicshadow")
        val IF_SETVFLIP = InstructionDefinition(2118, "if_setvflip")
        val IF_SETHFLIP = InstructionDefinition(2119, "if_sethflip")
        val IF_SETSCROLLSIZE = InstructionDefinition(2120, "if_setscrollsize")
        val IF_RESUME_PAUSEBUTTON = InstructionDefinition(2121, "if_resume_pausebutton")
        val IF_SETFILLCOLOUR = InstructionDefinition(2123, "if_setfillcolour")
        val IF_SETLINEDIRECTION = InstructionDefinition(2126, "if_setlinedirection")
        val IF_SETOBJECT = InstructionDefinition(2200, "if_setobject")
        val IF_SETNPCHEAD = InstructionDefinition(2201, "if_setnpchead")
        val IF_SETPLAYERHEAD_SELF = InstructionDefinition(2202, "if_setplayerhead_self")
        val IF_SETOBJECT_NONUM = InstructionDefinition(2205, "if_setobject_nonum")
        val IF_SETOBJECT_ALWAYS_NUM = InstructionDefinition(2212, "if_setobject_always_num")
        val IF_SETOP = InstructionDefinition(2300, "if_setop")
        val IF_SETDRAGGABLE = InstructionDefinition(2301, "if_setdraggable")
        val IF_SETDRAGGABLEBEHAVIOR = InstructionDefinition(2302, "if_setdraggablebehavior")
        val IF_SETDRAGDEADZONE = InstructionDefinition(2303, "if_setdragdeadzone")
        val IF_SETDRAGDEADTIME = InstructionDefinition(2304, "if_setdragdeadtime")
        val IF_SETOPBASE = InstructionDefinition(2305, "if_setopbase")
        val IF_SETTARGETVERB = InstructionDefinition(2306, "if_settargetverb")
        val IF_CLEAROPS = InstructionDefinition(2307, "if_clearops")
        val IF_SETOPKEY = InstructionDefinition(2350, "if_setopkey")
        val IF_SETOPTKEY = InstructionDefinition(2351, "if_setoptkey")
        val IF_SETOPKEYRATE = InstructionDefinition(2352, "if_setopkeyrate")
        val IF_SETOPTKEYRATE = InstructionDefinition(2353, "if_setoptkeyrate")
        val IF_SETOPKEYIGNOREHELD = InstructionDefinition(2354, "if_setopkeyignoreheld")
        val IF_SETOPTKEYIGNOREHELD = InstructionDefinition(2355, "if_setoptkeyignoreheld")
        val IF_SETONCLICK = InstructionDefinition(2400, "if_setonclick")
        val IF_SETONHOLD = InstructionDefinition(2401, "if_setonhold")
        val IF_SETONRELEASE = InstructionDefinition(2402, "if_setonrelease")
        val IF_SETONMOUSEOVER = InstructionDefinition(2403, "if_setonmouseover")
        val IF_SETONMOUSELEAVE = InstructionDefinition(2404, "if_setonmouseleave")
        val IF_SETONDRAG = InstructionDefinition(2405, "if_setondrag")
        val IF_SETONTARGETLEAVE = InstructionDefinition(2406, "if_setontargetleave")
        val IF_SETONVARTRANSMIT = InstructionDefinition(2407, "if_setonvartransmit")
        val IF_SETONTIMER = InstructionDefinition(2408, "if_setontimer")
        val IF_SETONOP = InstructionDefinition(2409, "if_setonop")
        val IF_SETONDRAGCOMPLETE = InstructionDefinition(2410, "if_setondragcomplete")
        val IF_SETONCLICKREPEAT = InstructionDefinition(2411, "if_setonclickrepeat")
        val IF_SETONMOUSEREPEAT = InstructionDefinition(2412, "if_setonmouserepeat")
        val IF_SETONINVTRANSMIT = InstructionDefinition(2414, "if_setoninvtransmit")
        val IF_SETONSTATTRANSMIT = InstructionDefinition(2415, "if_setonstattransmit")
        val IF_SETONTARGETENTER = InstructionDefinition(2416, "if_setontargetenter")
        val IF_SETONSCROLLWHEEL = InstructionDefinition(2417, "if_setonscrollwheel")
        val IF_SETONCHATTRANSMIT = InstructionDefinition(2418, "if_setonchattransmit")
        val IF_SETONKEY = InstructionDefinition(2419, "if_setonkey")
        val IF_SETONFRIENDTRANSMIT = InstructionDefinition(2420, "if_setonfriendtransmit")
        val IF_SETONCLANTRANSMIT = InstructionDefinition(2421, "if_setonclantransmit")
        val IF_SETONMISCTRANSMIT = InstructionDefinition(2422, "if_setonmisctransmit")
        val IF_SETONDIALOGABORT = InstructionDefinition(2423, "if_setondialogabort")
        val IF_SETONSUBCHANGE = InstructionDefinition(2424, "if_setonsubchange")
        val IF_SETONSTOCKTRANSMIT = InstructionDefinition(2425, "if_setonstocktransmit")
        val IF_SETONRESIZE = InstructionDefinition(2427, "if_setonresize")
        val IF_GETX = InstructionDefinition(2500, "if_getx")
        val IF_GETY = InstructionDefinition(2501, "if_gety")
        val IF_GETWIDTH = InstructionDefinition(2502, "if_getwidth")
        val IF_GETHEIGHT = InstructionDefinition(2503, "if_getheight")
        val IF_GETHIDE = InstructionDefinition(2504, "if_gethide")
        val IF_GETLAYER = InstructionDefinition(2505, "if_getlayer")
        val IF_GETSCROLLX = InstructionDefinition(2600, "if_getscrollx")
        val IF_GETSCROLLY = InstructionDefinition(2601, "if_getscrolly")
        val IF_GETTEXT = InstructionDefinition(2602, "if_gettext")
        val IF_GETSCROLLWIDTH = InstructionDefinition(2603, "if_getscrollwidth")
        val IF_GETSCROLLHEIGHT = InstructionDefinition(2604, "if_getscrollheight")
        val IF_GETMODELZOOM = InstructionDefinition(2605, "if_getmodelzoom")
        val IF_GETMODELANGLE_X = InstructionDefinition(2606, "if_getmodelangle_x")
        val IF_GETMODELANGLE_Z = InstructionDefinition(2607, "if_getmodelangle_z")
        val IF_GETMODELANGLE_Y = InstructionDefinition(2608, "if_getmodelangle_y")
        val IF_GETTRANS = InstructionDefinition(2609, "if_gettrans")
        val IF_GETCOLOUR = InstructionDefinition(2611, "if_getcolour")
        val IF_GETFILLCOLOUR = InstructionDefinition(2612, "if_getfillcolour")
        val IF_GETINVOBJECT = InstructionDefinition(2700, "if_getinvobject")
        val IF_GETINVCOUNT = InstructionDefinition(2701, "if_getinvcount")
        val IF_HASSUB = InstructionDefinition(2702, "if_hassub")
        val IF_GETTOP = InstructionDefinition(2706, "if_gettop")
        val IF_GETTARGETMASK = InstructionDefinition(2800, "if_gettargetmask")
        val IF_GETOP = InstructionDefinition(2801, "if_getop")
        val IF_GETOPBASE = InstructionDefinition(2802, "if_getopbase")
        val IF_CALLONRESIZE = InstructionDefinition(2927, "if_callonresize")
        val MES = InstructionDefinition(3100, "mes")
        val ANIM = InstructionDefinition(3101, "anim")
        val IF_CLOSE = InstructionDefinition(3103, "if_close")
        val RESUME_COUNTDIALOG = InstructionDefinition(3104, "resume_countdialog")
        val RESUME_NAMEDIALOG = InstructionDefinition(3105, "resume_namedialog")
        val RESUME_STRINGDIALOG = InstructionDefinition(3106, "resume_stringdialog")
        val OPPLAYER = InstructionDefinition(3107, "opplayer")
        val IF_DRAGPICKUP = InstructionDefinition(3108, "if_dragpickup")
        val CC_DRAGPICKUP = InstructionDefinition(3109, "cc_dragpickup")
        val MOUSECAM = InstructionDefinition(3110, "mousecam")
        val GETREMOVEROOFS = InstructionDefinition(3111, "getremoveroofs")
        val SETREMOVEROOFS = InstructionDefinition(3112, "setremoveroofs")
        val OPENURL = InstructionDefinition(3113, "openurl")
        val RESUME_OBJDIALOG = InstructionDefinition(3115, "resume_objdialog")
        val BUG_REPORT = InstructionDefinition(3116, "bug_report")
        val SETSHIFTCLICKDROP = InstructionDefinition(3117, "setshiftclickdrop")
        val SETSHOWMOUSEOVERTEXT = InstructionDefinition(3118, "setshowmouseovertext")
        val RENDERSELF = InstructionDefinition(3119, "renderself")
        val SETSHOWMOUSECROSS = InstructionDefinition(3125, "setshowmousecross")
        val SETSHOWLOADINGMESSAGES = InstructionDefinition(3126, "setshowloadingmessages")
        val SETTAPTODROP = InstructionDefinition(3127, "settaptodrop")
        val GETTAPTODROP = InstructionDefinition(3128, "gettaptodrop")
        val GETCANVASSIZE = InstructionDefinition(3132, "getcanvassize")
        val SETHIDEUSERNAME = InstructionDefinition(3141, "sethideusername")
        val GETHIDEUSERNAME = InstructionDefinition(3142, "gethideusername")
        val SETREMEMBERUSERNAME = InstructionDefinition(3143, "setrememberusername")
        val GETREMEMBERUSERNAME = InstructionDefinition(3144, "getrememberusername")
        val SOUND_SYNTH = InstructionDefinition(3200, "sound_synth")
        val SOUND_SONG = InstructionDefinition(3201, "sound_song")
        val SOUND_JINGLE = InstructionDefinition(3202, "sound_jingle")
        val CLIENTCLOCK = InstructionDefinition(3300, "clientclock")
        val INV_GETOBJ = InstructionDefinition(3301, "inv_getobj")
        val INV_GETNUM = InstructionDefinition(3302, "inv_getnum")
        val INV_TOTAL = InstructionDefinition(3303, "inv_total")
        val INV_SIZE = InstructionDefinition(3304, "inv_size")
        val STAT = InstructionDefinition(3305, "stat")
        val STAT_BASE = InstructionDefinition(3306, "stat_base")
        val STAT_XP = InstructionDefinition(3307, "stat_xp")
        val COORD = InstructionDefinition(3308, "coord")
        val COORDX = InstructionDefinition(3309, "coordx")
        val COORDZ = InstructionDefinition(3310, "coordz")
        val COORDY = InstructionDefinition(3311, "coordy")
        val MAP_MEMBERS = InstructionDefinition(3312, "map_members")
        val INVOTHER_GETOBJ = InstructionDefinition(3313, "invother_getobj")
        val INVOTHER_GETNUM = InstructionDefinition(3314, "invother_getnum")
        val INVOTHER_TOTAL = InstructionDefinition(3315, "invother_total")
        val STAFFMODLEVEL = InstructionDefinition(3316, "staffmodlevel")
        val REBOOTTIMER = InstructionDefinition(3317, "reboottimer")
        val MAP_WORLD = InstructionDefinition(3318, "map_world")
        val RUNENERGY_VISIBLE = InstructionDefinition(3321, "runenergy_visible")
        val RUNWEIGHT_VISIBLE = InstructionDefinition(3322, "runweight_visible")
        val PLAYERMOD = InstructionDefinition(3323, "playermod")
        val WORLDFLAGS = InstructionDefinition(3324, "worldflags")
        val MOVECOORD = InstructionDefinition(3325, "movecoord")
        val ENUM_STRING = InstructionDefinition(3400, "enum_string")
        val ENUM = InstructionDefinition(3408, "enum")
        val ENUM_GETOUTPUTCOUNT = InstructionDefinition(3411, "enum_getoutputcount")
        val FRIEND_COUNT = InstructionDefinition(3600, "friend_count")
        val FRIEND_GETNAME = InstructionDefinition(3601, "friend_getname")
        val FRIEND_GETWORLD = InstructionDefinition(3602, "friend_getworld")
        val FRIEND_GETRANK = InstructionDefinition(3603, "friend_getrank")
        val FRIEND_SETRANK = InstructionDefinition(3604, "friend_setrank")
        val FRIEND_ADD = InstructionDefinition(3605, "friend_add")
        val FRIEND_DEL = InstructionDefinition(3606, "friend_del")
        val IGNORE_ADD = InstructionDefinition(3607, "ignore_add")
        val IGNORE_DEL = InstructionDefinition(3608, "ignore_del")
        val FRIEND_TEST = InstructionDefinition(3609, "friend_test")
        val CLAN_GETCHATDISPLAYNAME = InstructionDefinition(3611, "clan_getchatdisplayname")
        val CLAN_GETCHATCOUNT = InstructionDefinition(3612, "clan_getchatcount")
        val CLAN_GETCHATUSERNAME = InstructionDefinition(3613, "clan_getchatusername")
        val CLAN_GETCHATUSERWORLD = InstructionDefinition(3614, "clan_getchatuserworld")
        val CLAN_GETCHATUSERRANK = InstructionDefinition(3615, "clan_getchatuserrank")
        val CLAN_GETCHATMINKICK = InstructionDefinition(3616, "clan_getchatminkick")
        val CLAN_KICKUSER = InstructionDefinition(3617, "clan_kickuser")
        val CLAN_GETCHATRANK = InstructionDefinition(3618, "clan_getchatrank")
        val CLAN_JOINCHAT = InstructionDefinition(3619, "clan_joinchat")
        val CLAN_LEAVECHAT = InstructionDefinition(3620, "clan_leavechat")
        val IGNORE_COUNT = InstructionDefinition(3621, "ignore_count")
        val IGNORE_GETNAME = InstructionDefinition(3622, "ignore_getname")
        val IGNORE_TEST = InstructionDefinition(3623, "ignore_test")
        val CLAN_ISSELF = InstructionDefinition(3624, "clan_isself")
        val CLAN_GETCHATOWNERNAME = InstructionDefinition(3625, "clan_getchatownername")
        val CLAN_ISFRIEND = InstructionDefinition(3626, "clan_isfriend")
        val CLAN_ISIGNORE = InstructionDefinition(3627, "clan_isignore")
        val STOCKMARKET_GETOFFERTYPE = InstructionDefinition(3903, "stockmarket_getoffertype")
        val STOCKMARKET_GETOFFERITEM = InstructionDefinition(3904, "stockmarket_getofferitem")
        val STOCKMARKET_GETOFFERPRICE = InstructionDefinition(3905, "stockmarket_getofferprice")
        val STOCKMARKET_GETOFFERCOUNT = InstructionDefinition(3906, "stockmarket_getoffercount")
        val STOCKMARKET_GETOFFERCOMPLETEDCOUNT =
            InstructionDefinition(3907, "stockmarket_getoffercompletedcount")
        val STOCKMARKET_GETOFFERCOMPLETEDGOLD =
            InstructionDefinition(3908, "stockmarket_getoffercompletedgold")
        val STOCKMARKET_ISOFFEREMPTY = InstructionDefinition(3910, "stockmarket_isofferempty")
        val STOCKMARKET_ISOFFERSTABLE = InstructionDefinition(3911, "stockmarket_isofferstable")
        val STOCKMARKET_ISOFFERFINISHED = InstructionDefinition(3912, "stockmarket_isofferfinished")
        val STOCKMARKET_ISOFFERADDING = InstructionDefinition(3913, "stockmarket_isofferadding")
        val TRADINGPOST_SORTBY_NAME = InstructionDefinition(3914, "tradingpost_sortby_name")
        val TRADINGPOST_SORTBY_PRICE = InstructionDefinition(3915, "tradingpost_sortby_price")
        val TRADINGPOST_SORTFILTERBY_WORLD =
            InstructionDefinition(3916, "tradingpost_sortfilterby_world")
        val TRADINGPOST_SORTBY_AGE = InstructionDefinition(3917, "tradingpost_sortby_age")
        val TRADINGPOST_SORTBY_COUNT = InstructionDefinition(3918, "tradingpost_sortby_count")
        val TRADINGPOST_GETTOTALOFFERS = InstructionDefinition(3919, "tradingpost_gettotaloffers")
        val TRADINGPOST_GETOFFERWORLD = InstructionDefinition(3920, "tradingpost_getofferworld")
        val TRADINGPOST_GETOFFERNAME = InstructionDefinition(3921, "tradingpost_getoffername")
        val TRADINGPOST_GETOFFERPREVIOUSNAME =
            InstructionDefinition(3922, "tradingpost_getofferpreviousname")
        val TRADINGPOST_GETOFFERAGE = InstructionDefinition(3923, "tradingpost_getofferage")
        val TRADINGPOST_GETOFFERCOUNT = InstructionDefinition(3924, "tradingpost_getoffercount")
        val TRADINGPOST_GETOFFERPRICE = InstructionDefinition(3925, "tradingpost_getofferprice")
        val TRADINGPOST_GETOFFERITEM = InstructionDefinition(3926, "tradingpost_getofferitem")
        val ADD = InstructionDefinition(4000, "add")
        val SUB = InstructionDefinition(4001, "sub")
        val MULTIPLY = InstructionDefinition(4002, "multiply")
        val DIV = InstructionDefinition(4003, "div")
        val RANDOM = InstructionDefinition(4004, "random")
        val RANDOMINC = InstructionDefinition(4005, "randominc")
        val INTERPOLATE = InstructionDefinition(4006, "interpolate")
        val ADDPERCENT = InstructionDefinition(4007, "addpercent")
        val SETBIT = InstructionDefinition(4008, "setbit")
        val CLEARBIT = InstructionDefinition(4009, "clearbit")
        val TESTBIT = InstructionDefinition(4010, "testbit")
        val MOD = InstructionDefinition(4011, "mod")
        val POW = InstructionDefinition(4012, "pow")
        val INVPOW = InstructionDefinition(4013, "invpow")
        val AND = InstructionDefinition(4014, "and")
        val OR = InstructionDefinition(4015, "or")
        val SCALE = InstructionDefinition(4018, "scale")
        val APPEND_NUM = InstructionDefinition(4100, "append_num")
        val APPEND = InstructionDefinition(4101, "append")
        val APPEND_SIGNNUM = InstructionDefinition(4102, "append_signnum")
        val LOWERCASE = InstructionDefinition(4103, "lowercase")
        val FROMDATE = InstructionDefinition(4104, "fromdate")
        val TEXT_GENDER = InstructionDefinition(4105, "text_gender")
        val TOSTRING = InstructionDefinition(4106, "tostring")
        val COMPARE = InstructionDefinition(4107, "compare")
        val PARAHEIGHT = InstructionDefinition(4108, "paraheight")
        val PARAWIDTH = InstructionDefinition(4109, "parawidth")
        val TEXT_SWITCH = InstructionDefinition(4110, "text_switch")
        val ESCAPE = InstructionDefinition(4111, "escape")
        val APPEND_CHAR = InstructionDefinition(4112, "append_char")
        val CHAR_ISPRINTABLE = InstructionDefinition(4113, "char_isprintable")
        val CHAR_ISALPHANUMERIC = InstructionDefinition(4114, "char_isalphanumeric")
        val CHAR_ISALPHA = InstructionDefinition(4115, "char_isalpha")
        val CHAR_ISNUMERIC = InstructionDefinition(4116, "char_isnumeric")
        val STRING_LENGTH = InstructionDefinition(4117, "string_length")
        val SUBSTRING = InstructionDefinition(4118, "substring")
        val REMOVETAGS = InstructionDefinition(4119, "removetags")
        val STRING_INDEXOF_CHAR = InstructionDefinition(4120, "string_indexof_char")
        val STRING_INDEXOF_STRING = InstructionDefinition(4121, "string_indexof_string")
        val OC_NAME = InstructionDefinition(4200, "oc_name")
        val OC_OP = InstructionDefinition(4201, "oc_op")
        val OC_IOP = InstructionDefinition(4202, "oc_iop")
        val OC_COST = InstructionDefinition(4203, "oc_cost")
        val OC_STACKABLE = InstructionDefinition(4204, "oc_stackable")
        val OC_CERT = InstructionDefinition(4205, "oc_cert")
        val OC_UNCERT = InstructionDefinition(4206, "oc_uncert")
        val OC_MEMBERS = InstructionDefinition(4207, "oc_members")
        val OC_PLACEHOLDER = InstructionDefinition(4208, "oc_placeholder")
        val OC_UNPLACEHOLDER = InstructionDefinition(4209, "oc_unplaceholder")
        val OC_FIND = InstructionDefinition(4210, "oc_find")
        val OC_FINDNEXT = InstructionDefinition(4211, "oc_findnext")
        val OC_FINDRESET = InstructionDefinition(4212, "oc_findreset")
        val CHAT_GETFILTER_PUBLIC = InstructionDefinition(5000, "chat_getfilter_public")
        val CHAT_SETFILTER = InstructionDefinition(5001, "chat_setfilter")
        val CHAT_SENDABUSEREPORT = InstructionDefinition(5002, "chat_sendabusereport")
        val CHAT_GETHISTORY_BYTYPEANDLINE = InstructionDefinition(5003, "chat_gethistory_bytypeandline")
        val CHAT_GETHISTORY_BYUID = InstructionDefinition(5004, "chat_gethistory_byuid")
        val CHAT_GETFILTER_PRIVATE = InstructionDefinition(5005, "chat_getfilter_private")
        val CHAT_SENDPUBLIC = InstructionDefinition(5008, "chat_sendpublic")
        val CHAT_SENDPRIVATE = InstructionDefinition(5009, "chat_sendprivate")
        val CHAT_PLAYERNAME = InstructionDefinition(5015, "chat_playername")
        val CHAT_GETFILTER_TRADE = InstructionDefinition(5016, "chat_getfilter_trade")
        val CHAT_GETHISTORYLENGTH = InstructionDefinition(5017, "chat_gethistorylength")
        val CHAT_GETNEXTUID = InstructionDefinition(5018, "chat_getnextuid")
        val CHAT_GETPREVUID = InstructionDefinition(5019, "chat_getprevuid")
        val DOCHEAT = InstructionDefinition(5020, "docheat")
        val CHAT_SETMESSAGEFILTER = InstructionDefinition(5021, "chat_setmessagefilter")
        val CHAT_GETMESSAGEFILTER = InstructionDefinition(5022, "chat_getmessagefilter")
        val GETWINDOWMODE = InstructionDefinition(5306, "getwindowmode")
        val SETWINDOWMODE = InstructionDefinition(5307, "setwindowmode")
        val GETDEFAULTWINDOWMODE = InstructionDefinition(5308, "getdefaultwindowmode")
        val SETDEFAULTWINDOWMODE = InstructionDefinition(5309, "setdefaultwindowmode")
        val CAM_FORCEANGLE = InstructionDefinition(5504, "cam_forceangle")
        val CAM_GETANGLE_XA = InstructionDefinition(5505, "cam_getangle_xa")
        val CAM_GETANGLE_YA = InstructionDefinition(5506, "cam_getangle_ya")
        val CAM_SETFOLLOWHEIGHT = InstructionDefinition(5530, "cam_setfollowheight")
        val CAM_GETFOLLOWHEIGHT = InstructionDefinition(5531, "cam_getfollowheight")
        val LOGOUT = InstructionDefinition(5630, "logout")
        val VIEWPORT_SETFOV = InstructionDefinition(6200, "viewport_setfov")
        val VIEWPORT_SETZOOM = InstructionDefinition(6201, "viewport_setzoom")
        val VIEWPORT_CLAMPFOV = InstructionDefinition(6202, "viewport_clampfov")
        val VIEWPORT_GETEFFECTIVESIZE = InstructionDefinition(6203, "viewport_geteffectivesize")
        val VIEWPORT_GETZOOM = InstructionDefinition(6204, "viewport_getzoom")
        val VIEWPORT_GETFOV = InstructionDefinition(6205, "viewport_getfov")
        val WORLDLIST_FETCH = InstructionDefinition(6500, "worldlist_fetch")
        val WORLDLIST_START = InstructionDefinition(6501, "worldlist_start")
        val WORLDLIST_NEXT = InstructionDefinition(6502, "worldlist_next")
        val WORLDLIST_SPECIFIC = InstructionDefinition(6506, "worldlist_specific")
        val WORLDLIST_SORT = InstructionDefinition(6507, "worldlist_sort")
        val SETFOLLOWEROPSLOWPRIORITY = InstructionDefinition(6512, "setfolloweropslowpriority")
        val NC_PARAM = InstructionDefinition(6513, "nc_param")
        val LC_PARAM = InstructionDefinition(6514, "lc_param")
        val OC_PARAM = InstructionDefinition(6515, "oc_param")
        val STRUCT_PARAM = InstructionDefinition(6516, "struct_param")
        val ON_MOBILE = InstructionDefinition(6518, "on_mobile")
        val CLIENTTYPE = InstructionDefinition(6519, "clienttype")
        val BATTERYLEVEL = InstructionDefinition(6524, "batterylevel")
        val BATTERYCHARGING = InstructionDefinition(6525, "batterycharging")
        val WIFIAVAILABLE = InstructionDefinition(6526, "wifiavailable")
        val WORLDMAP_GETMAPNAME = InstructionDefinition(6601, "worldmap_getmapname")
        val WORLDMAP_SETMAP = InstructionDefinition(6602, "worldmap_setmap")
        val WORLDMAP_GETZOOM = InstructionDefinition(6603, "worldmap_getzoom")
        val WORLDMAP_SETZOOM = InstructionDefinition(6604, "worldmap_setzoom")
        val WORLDMAP_ISLOADED = InstructionDefinition(6605, "worldmap_isloaded")
        val WORLDMAP_JUMPTODISPLAYCOORD = InstructionDefinition(6606, "worldmap_jumptodisplaycoord")
        val WORLDMAP_JUMPTODISPLAYCOORD_INSTANT =
            InstructionDefinition(6607, "worldmap_jumptodisplaycoord_instant")
        val WORLDMAP_JUMPTOSOURCECOORD = InstructionDefinition(6608, "worldmap_jumptosourcecoord")
        val WORLDMAP_JUMPTOSOURCECOORD_INSTANT =
            InstructionDefinition(6609, "worldmap_jumptosourcecoord_instant")
        val WORLDMAP_GETDISPLAYPOSITION = InstructionDefinition(6610, "worldmap_getdisplayposition")
        val WORLDMAP_GETCONFIGORIGIN = InstructionDefinition(6611, "worldmap_getconfigorigin")
        val WORLDMAP_GETCONFIGSIZE = InstructionDefinition(6612, "worldmap_getconfigsize")
        val WORLDMAP_GETCONFIGBOUNDS = InstructionDefinition(6613, "worldmap_getconfigbounds")
        val WORLDMAP_GETCONFIGZOOM = InstructionDefinition(6614, "worldmap_getconfigzoom")
        val WORLDMAP_GETCURRENTMAP = InstructionDefinition(6616, "worldmap_getcurrentmap")
        val WORLDMAP_GETDISPLAYCOORD = InstructionDefinition(6617, "worldmap_getdisplaycoord")
        val WORLDMAP_COORDINMAP = InstructionDefinition(6621, "worldmap_coordinmap")
        val WORLDMAP_GETSIZE = InstructionDefinition(6622, "worldmap_getsize")
        val WORLDMAP_PERPETUALFLASH = InstructionDefinition(6628, "worldmap_perpetualflash")
        val WORLDMAP_FLASHELEMENT = InstructionDefinition(6629, "worldmap_flashelement")
        val WORLDMAP_FLASHELEMENTCATEGORY = InstructionDefinition(6630, "worldmap_flashelementcategory")
        val WORLDMAP_STOPCURRENTFLASHES = InstructionDefinition(6631, "worldmap_stopcurrentflashes")
        val WORLDMAP_DISABLEELEMENTS = InstructionDefinition(6632, "worldmap_disableelements")
        val WORLDMAP_DISABLEELEMENT = InstructionDefinition(6633, "worldmap_disableelement")
        val WORLDMAP_DISABLEELEMENTCATEGORY =
            InstructionDefinition(6634, "worldmap_disableelementcategory")
        val WORLDMAP_GETDISABLEELEMENTS = InstructionDefinition(6635, "worldmap_getdisableelements")
        val WORLDMAP_GETDISABLEELEMENT = InstructionDefinition(6636, "worldmap_getdisableelement")
        val WORLDMAP_GETDISABLEELEMENTCATEGORY =
            InstructionDefinition(6637, "worldmap_getdisableelementcategory")
        val WORLDMAP_LISTELEMENT_START = InstructionDefinition(6639, "worldmap_listelement_start")
        val WORLDMAP_LISTELEMENT_NEXT = InstructionDefinition(6640, "worldmap_listelement_next")
        val MEC_TEXT = InstructionDefinition(6693, "mec_text")
        val MEC_TEXTSIZE = InstructionDefinition(6694, "mec_textsize")
        val MEC_CATEGORY = InstructionDefinition(6695, "mec_category")
        val MEC_SPRITE = InstructionDefinition(6696, "mec_sprite")

        init {
            val instructions = setOf(
                ICONST,
                GET_VARP,
                SET_VARP,
                SCONST,
                JUMP,
                IF_ICMPNE,
                IF_ICMPEQ,
                IF_ICMPLT,
                IF_ICMPGT,
                RETURN,
                GET_VARBIT,
                SET_VARBIT,
                IF_ICMPLE,
                IF_ICMPGE,
                ILOAD,
                ISTORE,
                SLOAD,
                SSTORE,
                JOIN_STRING,
                POP_INT,
                POP_STRING,
                INVOKE,
                GET_VARC_INT,
                SET_VARC_INT,
                DEFINE_ARRAY,
                GET_ARRAY_INT,
                SET_ARRAY_INT,
                GET_VARC_STRING_OLD,
                SET_VARC_STRING_OLD,
                GET_VARC_STRING,
                SET_VARC_STRING,
                SWITCH,
                CC_CREATE,
                CC_DELETE,
                CC_DELETEALL,
                CC_FIND,
                IF_FIND,
                CC_SETPOSITION,
                CC_SETSIZE,
                CC_SETHIDE,
                CC_SETNOCLICKTHROUGH,
                CC_SETSCROLLPOS,
                CC_SETCOLOUR,
                CC_SETFILL,
                CC_SETTRANS,
                CC_SETLINEWID,
                CC_SETGRAPHIC,
                CC_SET2DANGLE,
                CC_SETTILING,
                CC_SETMODEL,
                CC_SETMODELANGLE,
                CC_SETMODELANIM,
                CC_SETMODELORTHOG,
                CC_SETTEXT,
                CC_SETTEXTFONT,
                CC_SETTEXTALIGN,
                CC_SETTEXTSHADOW,
                CC_SETOUTLINE,
                CC_SETGRAPHICSHADOW,
                CC_SETVFLIP,
                CC_SETHFLIP,
                CC_SETSCROLLSIZE,
                CC_RESUME_PAUSEBUTTON,
                CC_SETFILLCOLOUR,
                CC_SETLINEDIRECTION,
                CC_SETOBJECT,
                CC_SETNPCHEAD,
                CC_SETPLAYERHEAD_SELF,
                CC_SETOBJECT_NONUM,
                CC_SETOBJECT_ALWAYS_NUM,
                CC_SETOP,
                CC_SETDRAGGABLE,
                CC_SETDRAGGABLEBEHAVIOR,
                CC_SETDRAGDEADZONE,
                CC_SETDRAGDEADTIME,
                CC_SETOPBASE,
                CC_SETTARGETVERB,
                CC_CLEAROPS,
                CC_SETONCLICK,
                CC_SETONHOLD,
                CC_SETONRELEASE,
                CC_SETONMOUSEOVER,
                CC_SETONMOUSELEAVE,
                CC_SETONDRAG,
                CC_SETONTARGETLEAVE,
                CC_SETONVARTRANSMIT,
                CC_SETONTIMER,
                CC_SETONOP,
                CC_SETONDRAGCOMPLETE,
                CC_SETONCLICKREPEAT,
                CC_SETONMOUSEREPEAT,
                CC_SETONINVTRANSMIT,
                CC_SETONSTATTRANSMIT,
                CC_SETONTARGETENTER,
                CC_SETONSCROLLWHEEL,
                CC_SETONCHATTRANSMIT,
                CC_SETONKEY,
                CC_SETONFRIENDTRANSMIT,
                CC_SETONCLANTRANSMIT,
                CC_SETONMISCTRANSMIT,
                CC_SETONDIALOGABORT,
                CC_SETONSUBCHANGE,
                CC_SETONSTOCKTRANSMIT,
                CC_SETONRESIZE,
                CC_GETX,
                CC_GETY,
                CC_GETWIDTH,
                CC_GETHEIGHT,
                CC_GETHIDE,
                CC_GETLAYER,
                CC_GETSCROLLX,
                CC_GETSCROLLY,
                CC_GETTEXT,
                CC_GETSCROLLWIDTH,
                CC_GETSCROLLHEIGHT,
                CC_GETMODELZOOM,
                CC_GETMODELANGLE_X,
                CC_GETMODELANGLE_Z,
                CC_GETMODELANGLE_Y,
                CC_GETTRANS,
                CC_GETCOLOUR,
                CC_GETFILLCOLOUR,
                CC_GETINVOBJECT,
                CC_GETINVCOUNT,
                CC_GETID,
                CC_GETTARGETMASK,
                CC_GETOP,
                CC_GETOPBASE,
                CC_CALLONRESIZE,
                IF_SETPOSITION,
                IF_SETSIZE,
                IF_SETHIDE,
                IF_SETNOCLICKTHROUGH,
                IF_SETSCROLLPOS,
                IF_SETCOLOUR,
                IF_SETFILL,
                IF_SETTRANS,
                IF_SETLINEWID,
                IF_SETGRAPHIC,
                IF_SET2DANGLE,
                IF_SETTILING,
                IF_SETMODEL,
                IF_SETMODELANGLE,
                IF_SETMODELANIM,
                IF_SETMODELORTHOG,
                IF_SETTEXT,
                IF_SETTEXTFONT,
                IF_SETTEXTALIGN,
                IF_SETTEXTSHADOW,
                IF_SETOUTLINE,
                IF_SETGRAPHICSHADOW,
                IF_SETVFLIP,
                IF_SETHFLIP,
                IF_SETSCROLLSIZE,
                IF_RESUME_PAUSEBUTTON,
                IF_SETFILLCOLOUR,
                IF_SETLINEDIRECTION,
                IF_SETOBJECT,
                IF_SETNPCHEAD,
                IF_SETPLAYERHEAD_SELF,
                IF_SETOBJECT_NONUM,
                IF_SETOBJECT_ALWAYS_NUM,
                IF_SETOP,
                IF_SETDRAGGABLE,
                IF_SETDRAGGABLEBEHAVIOR,
                IF_SETDRAGDEADZONE,
                IF_SETDRAGDEADTIME,
                IF_SETOPBASE,
                IF_SETTARGETVERB,
                IF_CLEAROPS,
                IF_SETOPKEY,
                IF_SETOPTKEY,
                IF_SETOPKEYRATE,
                IF_SETOPTKEYRATE,
                IF_SETOPKEYIGNOREHELD,
                IF_SETOPTKEYIGNOREHELD,
                IF_SETONCLICK,
                IF_SETONHOLD,
                IF_SETONRELEASE,
                IF_SETONMOUSEOVER,
                IF_SETONMOUSELEAVE,
                IF_SETONDRAG,
                IF_SETONTARGETLEAVE,
                IF_SETONVARTRANSMIT,
                IF_SETONTIMER,
                IF_SETONOP,
                IF_SETONDRAGCOMPLETE,
                IF_SETONCLICKREPEAT,
                IF_SETONMOUSEREPEAT,
                IF_SETONINVTRANSMIT,
                IF_SETONSTATTRANSMIT,
                IF_SETONTARGETENTER,
                IF_SETONSCROLLWHEEL,
                IF_SETONCHATTRANSMIT,
                IF_SETONKEY,
                IF_SETONFRIENDTRANSMIT,
                IF_SETONCLANTRANSMIT,
                IF_SETONMISCTRANSMIT,
                IF_SETONDIALOGABORT,
                IF_SETONSUBCHANGE,
                IF_SETONSTOCKTRANSMIT,
                IF_SETONRESIZE,
                IF_GETX,
                IF_GETY,
                IF_GETWIDTH,
                IF_GETHEIGHT,
                IF_GETHIDE,
                IF_GETLAYER,
                IF_GETSCROLLX,
                IF_GETSCROLLY,
                IF_GETTEXT,
                IF_GETSCROLLWIDTH,
                IF_GETSCROLLHEIGHT,
                IF_GETMODELZOOM,
                IF_GETMODELANGLE_X,
                IF_GETMODELANGLE_Z,
                IF_GETMODELANGLE_Y,
                IF_GETTRANS,
                IF_GETCOLOUR,
                IF_GETFILLCOLOUR,
                IF_GETINVOBJECT,
                IF_GETINVCOUNT,
                IF_HASSUB,
                IF_GETTOP,
                IF_GETTARGETMASK,
                IF_GETOP,
                IF_GETOPBASE,
                IF_CALLONRESIZE,
                MES,
                ANIM,
                IF_CLOSE,
                RESUME_COUNTDIALOG,
                RESUME_NAMEDIALOG,
                RESUME_STRINGDIALOG,
                OPPLAYER,
                IF_DRAGPICKUP,
                CC_DRAGPICKUP,
                MOUSECAM,
                GETREMOVEROOFS,
                SETREMOVEROOFS,
                OPENURL,
                RESUME_OBJDIALOG,
                BUG_REPORT,
                SETSHIFTCLICKDROP,
                SETSHOWMOUSEOVERTEXT,
                RENDERSELF,
                SETSHOWMOUSECROSS,
                SETSHOWLOADINGMESSAGES,
                SETTAPTODROP,
                GETTAPTODROP,
                GETCANVASSIZE,
                SETHIDEUSERNAME,
                GETHIDEUSERNAME,
                SETREMEMBERUSERNAME,
                GETREMEMBERUSERNAME,
                SOUND_SYNTH,
                SOUND_SONG,
                SOUND_JINGLE,
                CLIENTCLOCK,
                INV_GETOBJ,
                INV_GETNUM,
                INV_TOTAL,
                INV_SIZE,
                STAT,
                STAT_BASE,
                STAT_XP,
                COORD,
                COORDX,
                COORDZ,
                COORDY,
                MAP_MEMBERS,
                INVOTHER_GETOBJ,
                INVOTHER_GETNUM,
                INVOTHER_TOTAL,
                STAFFMODLEVEL,
                REBOOTTIMER,
                MAP_WORLD,
                RUNENERGY_VISIBLE,
                RUNWEIGHT_VISIBLE,
                PLAYERMOD,
                WORLDFLAGS,
                MOVECOORD,
                ENUM_STRING,
                ENUM,
                ENUM_GETOUTPUTCOUNT,
                FRIEND_COUNT,
                FRIEND_GETNAME,
                FRIEND_GETWORLD,
                FRIEND_GETRANK,
                FRIEND_SETRANK,
                FRIEND_ADD,
                FRIEND_DEL,
                IGNORE_ADD,
                IGNORE_DEL,
                FRIEND_TEST,
                CLAN_GETCHATDISPLAYNAME,
                CLAN_GETCHATCOUNT,
                CLAN_GETCHATUSERNAME,
                CLAN_GETCHATUSERWORLD,
                CLAN_GETCHATUSERRANK,
                CLAN_GETCHATMINKICK,
                CLAN_KICKUSER,
                CLAN_GETCHATRANK,
                CLAN_JOINCHAT,
                CLAN_LEAVECHAT,
                IGNORE_COUNT,
                IGNORE_GETNAME,
                IGNORE_TEST,
                CLAN_ISSELF,
                CLAN_GETCHATOWNERNAME,
                CLAN_ISFRIEND,
                CLAN_ISIGNORE,
                STOCKMARKET_GETOFFERTYPE,
                STOCKMARKET_GETOFFERITEM,
                STOCKMARKET_GETOFFERPRICE,
                STOCKMARKET_GETOFFERCOUNT,
                STOCKMARKET_GETOFFERCOMPLETEDCOUNT,
                STOCKMARKET_GETOFFERCOMPLETEDGOLD,
                STOCKMARKET_ISOFFEREMPTY,
                STOCKMARKET_ISOFFERSTABLE,
                STOCKMARKET_ISOFFERFINISHED,
                STOCKMARKET_ISOFFERADDING,
                TRADINGPOST_SORTBY_NAME,
                TRADINGPOST_SORTBY_PRICE,
                TRADINGPOST_SORTFILTERBY_WORLD,
                TRADINGPOST_SORTBY_AGE,
                TRADINGPOST_SORTBY_COUNT,
                TRADINGPOST_GETTOTALOFFERS,
                TRADINGPOST_GETOFFERWORLD,
                TRADINGPOST_GETOFFERNAME,
                TRADINGPOST_GETOFFERPREVIOUSNAME,
                TRADINGPOST_GETOFFERAGE,
                TRADINGPOST_GETOFFERCOUNT,
                TRADINGPOST_GETOFFERPRICE,
                TRADINGPOST_GETOFFERITEM,
                ADD,
                SUB,
                MULTIPLY,
                DIV,
                RANDOM,
                RANDOMINC,
                INTERPOLATE,
                ADDPERCENT,
                SETBIT,
                CLEARBIT,
                TESTBIT,
                MOD,
                POW,
                INVPOW,
                AND,
                OR,
                SCALE,
                APPEND_NUM,
                APPEND,
                APPEND_SIGNNUM,
                LOWERCASE,
                FROMDATE,
                TEXT_GENDER,
                TOSTRING,
                COMPARE,
                PARAHEIGHT,
                PARAWIDTH,
                TEXT_SWITCH,
                ESCAPE,
                APPEND_CHAR,
                CHAR_ISPRINTABLE,
                CHAR_ISALPHANUMERIC,
                CHAR_ISALPHA,
                CHAR_ISNUMERIC,
                STRING_LENGTH,
                SUBSTRING,
                REMOVETAGS,
                STRING_INDEXOF_CHAR,
                STRING_INDEXOF_STRING,
                OC_NAME,
                OC_OP,
                OC_IOP,
                OC_COST,
                OC_STACKABLE,
                OC_CERT,
                OC_UNCERT,
                OC_MEMBERS,
                OC_PLACEHOLDER,
                OC_UNPLACEHOLDER,
                OC_FIND,
                OC_FINDNEXT,
                OC_FINDRESET,
                CHAT_GETFILTER_PUBLIC,
                CHAT_SETFILTER,
                CHAT_SENDABUSEREPORT,
                CHAT_GETHISTORY_BYTYPEANDLINE,
                CHAT_GETHISTORY_BYUID,
                CHAT_GETFILTER_PRIVATE,
                CHAT_SENDPUBLIC,
                CHAT_SENDPRIVATE,
                CHAT_PLAYERNAME,
                CHAT_GETFILTER_TRADE,
                CHAT_GETHISTORYLENGTH,
                CHAT_GETNEXTUID,
                CHAT_GETPREVUID,
                DOCHEAT,
                CHAT_SETMESSAGEFILTER,
                CHAT_GETMESSAGEFILTER,
                GETWINDOWMODE,
                SETWINDOWMODE,
                GETDEFAULTWINDOWMODE,
                SETDEFAULTWINDOWMODE,
                CAM_FORCEANGLE,
                CAM_GETANGLE_XA,
                CAM_GETANGLE_YA,
                CAM_SETFOLLOWHEIGHT,
                CAM_GETFOLLOWHEIGHT,
                LOGOUT,
                VIEWPORT_SETFOV,
                VIEWPORT_SETZOOM,
                VIEWPORT_CLAMPFOV,
                VIEWPORT_GETEFFECTIVESIZE,
                VIEWPORT_GETZOOM,
                VIEWPORT_GETFOV,
                WORLDLIST_FETCH,
                WORLDLIST_START,
                WORLDLIST_NEXT,
                WORLDLIST_SPECIFIC,
                WORLDLIST_SORT,
                SETFOLLOWEROPSLOWPRIORITY,
                NC_PARAM,
                LC_PARAM,
                OC_PARAM,
                STRUCT_PARAM,
                ON_MOBILE,
                CLIENTTYPE,
                BATTERYLEVEL,
                BATTERYCHARGING,
                WIFIAVAILABLE,
                WORLDMAP_GETMAPNAME,
                WORLDMAP_SETMAP,
                WORLDMAP_GETZOOM,
                WORLDMAP_SETZOOM,
                WORLDMAP_ISLOADED,
                WORLDMAP_JUMPTODISPLAYCOORD,
                WORLDMAP_JUMPTODISPLAYCOORD_INSTANT,
                WORLDMAP_JUMPTOSOURCECOORD,
                WORLDMAP_JUMPTOSOURCECOORD_INSTANT,
                WORLDMAP_GETDISPLAYPOSITION,
                WORLDMAP_GETCONFIGORIGIN,
                WORLDMAP_GETCONFIGSIZE,
                WORLDMAP_GETCONFIGBOUNDS,
                WORLDMAP_GETCONFIGZOOM,
                WORLDMAP_GETCURRENTMAP,
                WORLDMAP_GETDISPLAYCOORD,
                WORLDMAP_COORDINMAP,
                WORLDMAP_GETSIZE,
                WORLDMAP_PERPETUALFLASH,
                WORLDMAP_FLASHELEMENT,
                WORLDMAP_FLASHELEMENTCATEGORY,
                WORLDMAP_STOPCURRENTFLASHES,
                WORLDMAP_DISABLEELEMENTS,
                WORLDMAP_DISABLEELEMENT,
                WORLDMAP_DISABLEELEMENTCATEGORY,
                WORLDMAP_GETDISABLEELEMENTS,
                WORLDMAP_GETDISABLEELEMENT,
                WORLDMAP_GETDISABLEELEMENTCATEGORY,
                WORLDMAP_LISTELEMENT_START,
                WORLDMAP_LISTELEMENT_NEXT,
                MEC_TEXT,
                MEC_TEXTSIZE,
                MEC_CATEGORY,
                MEC_SPRITE
            )
            instructions.forEach { instruction ->
                byOpcode[instruction.opcode] = instruction
                byName[instruction.name] = instruction
            }
        }
    }
}
