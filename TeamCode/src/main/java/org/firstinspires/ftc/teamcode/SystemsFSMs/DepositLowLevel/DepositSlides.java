package org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel;

import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;

public class DepositSlides {

    public enum State {
        TransferPos(DepositConstants.slideTransferPos),
        SpecIntakePos(DepositConstants.slideSpecIntakePos),
        SpecDepositPos(DepositConstants.slideSpecDepositPos),
        SampleDepositPos(DepositConstants.slideSampleDepositPos),
        Intermediate(0.0);

        public final double slidePos;


        State(double slidePos) {
            this.slidePos = slidePos;
        }
    }

    public State currentState;
    private State targetState;

    private DcMotorEx rightMotor;
    private DcMotorEx leftMotor;
    private Logger logger;

    private double spoolDiam = 4.0; // Spool Diameter in cm
    private double extensionLimit = DepositConstants.slideMaxExtension; // Extension Limit in cm

    private final double ticksToCm = (( 12.0 / 60.0 ) * Math.PI * spoolDiam) / (28); // Multiply ticks by this number to get distance in cm
    private final double cmToTicks = 1 / ticksToCm; // Multiply cm by this number to get distance in encoder ticks

    private double currentTicks = 0;
    private double currentCM = 0;
    private double rangedTarget = 0;

    private double power = 0;

    private double rightCurrent = 0;
    private double leftCurrent = 0;
    private double totalCurrent = 0;

    private double velocity = 0;

    private double leftTicks = 0;

    private double
            p = DepositConstants.sp,
            i = DepositConstants.si,
            d = DepositConstants.sd,
            f = DepositConstants.sf;

    private final PIDController controller = new PIDController(DepositConstants.sp, DepositConstants.si, DepositConstants.sd);

    public DepositSlides(Hardware hardware, Logger logger) {
        rightMotor = hardware.depositSlideRight;
        leftMotor = hardware.depositSlideLeft;

        this.logger = logger;
    }

    public void update() {
        currentTicks = leftMotor.getCurrentPosition();
        currentCM = -currentTicks * ticksToCm;
        velocity = -leftMotor.getVelocity(AngleUnit.DEGREES);

        leftTicks = leftMotor.getCurrentPosition();

        rightCurrent = rightMotor.getCurrent(CurrentUnit.MILLIAMPS);
        leftCurrent = leftMotor.getCurrent(CurrentUnit.MILLIAMPS);
        totalCurrent = rightCurrent + leftCurrent;

        findState();
    }

    public void command() {
        controller.setPID(p, i, d);

        rangedTarget = Math.min(Math.max(0, targetState.slidePos), extensionLimit);
        power = controller.calculate(currentCM * cmToTicks, rangedTarget * cmToTicks) + f;

        rightMotor.setPower(power);
        leftMotor.setPower(power);
    }

    public void log() {
        logger.logHeader("Deposit Slides");

        logger.logData("Current State", currentState, Logger.LogLevels.debug);
        logger.logData("Target State", targetState, Logger.LogLevels.debug);

        logger.logData("Depo Current CM", currentCM, Logger.LogLevels.debug);
        logger.logData("Depo Target CM", targetState.slidePos, Logger.LogLevels.debug);

        logger.logData("LeftCurrentCM", leftTicks * ticksToCm, Logger.LogLevels.debug);

        logger.logData("Depo Ranged Target CM", rangedTarget, Logger.LogLevels.developer);
        logger.logData("Depo Power", power, Logger.LogLevels.developer);
        logger.logData("Velocity (Degrees)", velocity, Logger.LogLevels.developer);
        logger.logData("Right Current", rightCurrent, Logger.LogLevels.developer);
        logger.logData("Left Current", leftCurrent, Logger.LogLevels.developer);
        logger.logData("Total Current", totalCurrent, Logger.LogLevels.developer);
        logger.logData("p", p, Logger.LogLevels.developer);
        logger.logData("i", i, Logger.LogLevels.developer);
        logger.logData("d", d, Logger.LogLevels.developer);
        logger.logData("f", f, Logger.LogLevels.developer);
    }

    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    public void setPID(double p, double i, double d, double f) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = f;
    }

    private void findState() {
        currentState = PosChecker.atLinearPos(currentCM, targetState.slidePos, DepositConstants.slidePositionTolerance) ? targetState : State.Intermediate;
    }

}
