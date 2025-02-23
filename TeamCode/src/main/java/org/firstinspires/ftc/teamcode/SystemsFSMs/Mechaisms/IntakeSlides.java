package org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Hardware.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;

public class IntakeSlides {

    private DcMotorEx motor;
    private Logger logger;

    private double spoolDiam = 4.0; // Spool Diameter in cm
    private double extensionLimit = IntakeConstants.maxExtensionPosition; // Extension Limit in cm

    private final double ticksToCm = (( 9 / 60.0 ) * Math.PI * spoolDiam) / (28); // Multiply ticks by this number to get distance in cm
    private final double cmToTicks = 1 / ticksToCm; // Multiply cm by this number to get distance in encoder ticks

    private double currentTicks = 0;
    private double currentCM = 0;
    private double targetCM = 0;
    private double rangedTarget = 0;
    private double power = 0;
    private double current = 0;
    private double velocity = 0;

    private boolean encoderResetEnabled;
    private boolean encoderReset = false;

    private double
            p = IntakeConstants.sp,
            i = IntakeConstants.si,
            d = IntakeConstants.sd;

    private PIDController controller = new PIDController(p, i, d);

    public IntakeSlides(Hardware hardware, Logger logger, boolean encoderResetEnabled) {
        motor = hardware.intakeSlideMotor;
        this.logger = logger;

        this.encoderResetEnabled = encoderResetEnabled;
    }

    public void update() {
        currentTicks = motor.getCurrentPosition();
        currentCM = currentTicks * ticksToCm;

        velocity = motor.getVelocity(AngleUnit.DEGREES);

        current = motor.getCurrent(CurrentUnit.MILLIAMPS);
    }

    public void command() {
        controller.setPID(p, i, d);

        rangedTarget = Math.min(Math.max(0, targetCM), extensionLimit);
        power = controller.calculate(currentCM * cmToTicks, rangedTarget * cmToTicks);

        // Re-Zero slides whenever target pos is zero
        if (targetCM == 0 && encoderResetEnabled) {
            if (!encoderReset) {
                power = IntakeConstants.intakeSlideZeroPower;

                // Once the motor stalls (based on current and velocity check), and position is near zero, reset the encoder and set encoderReset to true
                if (current >= 5500 && velocity == 0 && currentCM <= IntakeConstants.intakeSlidePositionTolerance) {
                    motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

                    encoderReset = true;
                }

            } else {
                power = Math.min(power, IntakeConstants.intakeSlideZeroStallPower);
            }

        } else { encoderReset = false; }

        motor.setPower(-power);
    }

    public void log() {
        logger.logHeader("Intake Slides");

        logger.logData("Current CM", currentCM, Logger.LogLevels.debug);
        logger.logData("Target CM", targetCM, Logger.LogLevels.debug);

        logger.logData("Ranged Target CM", rangedTarget, Logger.LogLevels.developer);
        logger.logData("Power", power, Logger.LogLevels.developer);
        logger.logData("Intake Slide Velocity", velocity, Logger.LogLevels.developer);
        logger.logData("Current", current, Logger.LogLevels.developer);
        logger.logData("p", p, Logger.LogLevels.developer);
        logger.logData("i", i, Logger.LogLevels.developer);
        logger.logData("d", d, Logger.LogLevels.developer);
    }

    public void setTargetCM(double target) {
        targetCM = target;
    }

    public void setPID(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }

    public double getPosition() {
        return currentCM;
    }

    public double getTargetCM() {
        return targetCM;
    }

    public double getVelocity() { return velocity; }

}