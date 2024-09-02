package org.mppsolartest.command;

import org.mppsolartest.model.Field;

import java.math.BigDecimal;
import java.util.List;

/**
 * Undocumented command, best explanation credits to coulomb at
 * https://forums.aeva.asn.au/viewtopic.php?title=pip4048ms-inverter&p=60229&t=4332#p60229
 *
 * The Q1 command (CRC 1B FC) seems to send back the following:
 *
 * (        All command responses start with the open parenthesis character.
 * AAAAA    Local inverter status (first field). This seems to be a bit field,
 *          commonly taking the values 0x3809 or similar (shows as 14345 decimal).
 *          Edit: this seems to become a count in seconds till the end of CV
 *          (absorb) charging, in firmware version 72.70.
 * BBBBB    ParaExistInfo first field. This always seems to be 00001, even with
 *          no parallel card installed.
 *          Edit: this seems to become a count in seconds till the end of float
 *          charging (when it will start CC (bulk) charging), in firmware version
 *          72.70.
 * CC       SccOkFlag. I assume that 1 means the SCC is powered and is
 *          communicating.
 * DD       AllowSccOnFlag.
 * EE       ChargeAverageCurrent. I'm not clear on what chargers are included.
 * FFF      SCC PWM temperature, in °C. From global variable wSccPWMTemp.
 * GGG      Inverter temperature, in °C. Presumed to be from the AC heatsink.
 * HHH      "Battery temperature". It seems that this must be the temperature
 *          reported by a sensor on the battery to bus inverter heatsink.
 * III      Transformer temperature. It's the result of calling _wTempDegreeTxt().
 *          Presumably also in °C.
 * JJ       Parallel mode: 0 1 2 mean NEw, SLave, MAster.
 * KK       FanLockStatus. I'd say 01 means fans are locked, 00 means not locked.
 * LLL      FanPWMDuty. No longer used. Always 000.
 * MMMM     "FanPWM", but is actually speed in percent. 0000 represents off, and
 *          0100 represents 100% duty cycle (flat out). However, on start-up, this
 *          value goes to 0100 without the fans roaring. 0030 (30%) seems to be
 *          the lowest speed, quite quiet. At 42% load, the fans went to 42%
 *          speed.
 * NNNN     SCC charge power, watts. This is one of the changes to firmware
 *          version 72.40 that is not present in version 52.30. In 72.40, the
 *          result of the call to _swGetSccChgPower() is divided by 10; in 52.30
 *          it is displayed as is. I suspect 52.30 would have displayed tenths of
 *          watts.
 * OOOO     ParaWarning. Presumably, some warning bitfield related to paralleled
 *          units.
 * PP.PP    SYNFreq. Wild guess: frequency of inverter after synchronising with
 *          the mains input.
 * QQ       Inverter charge status. This will likely be 10 for no charging, 11 for
 *          bulk stage, 12 for absorb, or 13 for float. However, bulk stage will
 *          usually report as 12, same as absorb. I don't know what the significance
 *          of the leading "1" digit is; I've always found it to be one, but
 *          the firmware calculates this value modulo 10 (stripping off the tens
 *          digit) a lot of the time.
 */
public class Q1 extends MapResponseCommand {
    @Override
    public String getCommand() {
        return "Q1";
    }

    @Override
    public List<Field> getFields() {
        return List.of(
                new Field<>("Q1 Local inverter status", Integer::valueOf),
                new Field<>("Q1 ParaExistInfo", Integer::valueOf),
                new Field<>("Q1 SccOkFlag", Integer::valueOf),
                new Field<>("Q1 AllowSccOnFlag", Integer::valueOf),
                new Field<>("Q1 ChargeAverageCurrent", Integer::valueOf),
                new Field<>("Q1 SCC PWM temperature in C°", Integer::valueOf),
                new Field<>("Q1 Inverter temperature (AC Heatsink?) in C°", Integer::valueOf),
                new Field<>("Q1 Battery temperature (Battery to bus inverter?) in C°", Integer::valueOf),
                new Field<>("Q1 Transformer temperature in C°", Integer::valueOf),
                new Field<>("Q1 Parallel mode", Integer::valueOf),
                new Field<>("Q1 FanLockStatus", Integer::valueOf),
                new Field<>("Q1 FanPWMDuty", Integer::valueOf),
                new Field<>("Q1 FanPWM (Percentage)", Integer::valueOf),
                new Field<>("Q1 SCC charge power", Integer::valueOf),
                new Field<>("Q1 ParaWarning", Integer::valueOf),
                new Field<>("Q1 SYNFreq", BigDecimal::new),
                new Field<>("Q1 Inverter charge status", Integer::valueOf),
                new Field<>("Q1 18", Integer::valueOf),
                new Field<>("Q1 19", Integer::valueOf),
                new Field<>("Q1 20", Integer::valueOf),
                new Field<>("Q1 21", Integer::valueOf),
                new Field<>("Q1 22", Integer::valueOf),
                new Field<>("Q1 23", BigDecimal::new),
                new Field<>("Q1 24", Integer::valueOf),
                new Field<>("Q1 25", Integer::valueOf),
                new Field<>("Q1 26", Integer::valueOf),
                new Field<>("Q1 27", Integer::valueOf)
        );
    }
}
