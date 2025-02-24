//TODO: Solve bug where intake stows imminently after attempting to go from depoyed
package org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms;

import com.qualcomm.robotcore.hardware.DigitalChannel;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Hardware.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Hardware.Drivers.GobildaBlindToucherV69;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.ColorUtils;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;

import java.util.Arrays;

public class SampleDetector {

    private final Logger logger;

    private double distance;
    private double r, g, b, a;
    private double h, hRaw, s, v;

    private boolean switchPressed = false;

    private int distanceBufferCounter = 0;
    private int hueBufferCounter = 0;
    private double[] distanceBuffer = new double[5];
    private double[] hueBuffer = new double[5];

    private final GobildaBlindToucherV69 colorSensor;

    private final DigitalChannel limitSwitch;

    public enum State {
        sampleDetected,
        noSampleDetected
    }

    public enum SampleColor {
        yellow,
        blue,
        red,
        unknown
    }

    public State state;
    public SampleColor color;

    public SampleDetector (Hardware hardware, Logger logger) {
        colorSensor = hardware.intakeCS;
        limitSwitch = hardware.intakeLS;
        this.logger = logger;
    }

    public void update() {
        // Updating distance buffer & counter
        distance = colorSensor.getDistance(DistanceUnit.MM);
        distanceBuffer[distanceBufferCounter] = distance;
        distanceBufferCounter += 1;
        distanceBufferCounter %= 5;

        switchPressed = limitSwitch.getState();

        findState();
    }

    public void log() {
        logger.logHeader("Sample Detector");

        logger.logData("State", state, Logger.LogLevels.debug);
        logger.logData("AO5 Distance", distanceAVG5(), Logger.LogLevels.debug);
        logger.logData("Color", color, Logger.LogLevels.debug);
        logger.logData("Distance", distance, Logger.LogLevels.developer);
        logger.logData("Distance Buffer", Arrays.toString(distanceBuffer), Logger.LogLevels.developer);
        logger.logData("Switch Pressed", switchPressed, Logger.LogLevels.developer);


        logger.logData("r", r, Logger.LogLevels.developer);
        logger.logData("g", g, Logger.LogLevels.developer);
        logger.logData("b", b, Logger.LogLevels.developer);
        logger.logData("a", a, Logger.LogLevels.developer);

        logger.logData("h", h, Logger.LogLevels.developer);
        logger.logData("s", s, Logger.LogLevels.developer);
        logger.logData("v", v, Logger.LogLevels.developer);
        logger.logData("Hue Buffer", Arrays.toString(hueBuffer), Logger.LogLevels.debug);

        logger.logData("Raw h", hRaw, Logger.LogLevels.developer);

    }

    // Clears distance buffer array
    public void clearDistanceBuffer() {
        distanceBuffer = new double[5];
    }

    // Clears hue buffer array
    private void clearHueBuffer() {
        hueBuffer = new double[5];
    }

    // Uses sensors to determine the state the sample detector should be in
    private void findState() {

        if (sampleDetected()) {
            state = State.sampleDetected;
            detectColor();
        } else {
            state = State.noSampleDetected;
            color = SampleColor.unknown;
            clearHueBuffer();
        }
    }

    // If no sample was previously detected, then both the limit switch and distance sensor must be triggered to detect a sample, if a sample was previously detected, only the distance requirement must be met
    private boolean sampleDetected() {
        if (state == State.sampleDetected) {
            return (distanceAVG5() <= IntakeConstants.detectionDistance);
        } else {
            return (distanceAVG5() <= IntakeConstants.detectionDistance) && switchPressed;
        }
    }

    // Simple Average of the last 5 distance readings, excludes zeros
    private double distanceAVG5() {
        double total = 0.00;
        double items = 0;

        for (double distance : distanceBuffer) {
            if (distance != 0) {
                total += distance;
                items += 1;
            }
        }

        items = items == 0 ? 1 : items;

        return total / items;
    }

    // Performs AO5, where of the last 5 reads, the lowest and highest are removed from the array and the remaining 3 are averaged
    private double hueAO5() {
        double min = 0;
        double max = 0;
        double total = 0;
        double items = 0;

        for (double hue : hueBuffer) {
            if (hue != 0) {
                total += hue;
                items += 1;

                min = Math.min(min, hue);
                max = Math.max(max, hue);
            }
        }

        return (total - min - max) / Math.max((items - 1), 1);

    }

    // Detects Colors of sample, if there are less than 5 color reads, then sample color is set to unknown
    private void detectColor()  {

        colorSensor.updateColors();

        r = colorSensor.red();
        g = colorSensor.green();
        b = colorSensor.blue();
        a = colorSensor.alpha();

        double r1 = ColorUtils.Clamp(r,0 ,IntakeConstants.maxR - 1);
        double g1 = ColorUtils.Clamp(g,0 ,IntakeConstants.maxG - 1);
        double b1 = ColorUtils.Clamp(b,0 ,IntakeConstants.maxB - 1);
        double a1 = ColorUtils.Clamp(a,0 ,IntakeConstants.maxA - 1);

        r1 = ColorUtils.Normalize(r1, 255, IntakeConstants.maxR);
        g1 = ColorUtils.Normalize(g1, 255, IntakeConstants.maxG);
        b1 = ColorUtils.Normalize(b1, 255, IntakeConstants.maxB);
        a1 = ColorUtils.Normalize(a1, 1, IntakeConstants.maxA);

        double[] hsv = ColorUtils.RGBAtoHSV(r1, g1, b1, a1);

        hueBuffer[hueBufferCounter % 5] = hsv[0];
        hueBufferCounter += 1;

        hRaw = hsv[0];

        hsv[0] = hueAO5();

        h = hsv[0];
        s = hsv[1];
        v = hsv[2];

        int items = 0;

        for (double hue : hueBuffer) {
            if (hue != 0) {
                items++;
            }
        }

        color = items == 5 ? ColorUtils.ClassifyColor(hsv) : SampleColor.unknown;
    }
}