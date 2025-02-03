package org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.AnalogServo;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Deposit;

public class Arm {

    public enum State {
        TransferPos(DepositConstants.armTransferPos, DepositConstants.armEncTransferPos),
        SpecIntakePos(DepositConstants.armSpecIntakePos, DepositConstants.armEncSpecIntakePos),
        SpecDepositPos(DepositConstants.armSpecDepositPos, DepositConstants.armEncSpecDepositPos),
        SampleDepositPos(DepositConstants.armSampleDepositPos, DepositConstants.armEncSampleDepositPos),
        Intermediate(0,0);

        public final double servoPos, encPos;

        State(double servoPos, double encPos) {
            this.servoPos = servoPos;
            this.encPos = encPos;
        }
    }

    private final Logger logger;

    private final AnalogServo servo;

    public State currentState;
    private State targetState;

    public double encPos;

    public Arm(Hardware hardware, Logger logger) {
        this.logger = logger;
        servo = new AnalogServo(hardware.armServo, hardware.armEnc);
    }

    public void update() {
        encPos = servo.getPos();
        findState();
    }

    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    public void command() {
        servo.setPos(targetState.servoPos);
    }

    public void log() {
        logger.logHeader("Arm");

        logger.logData("Current State", currentState, Logger.LogLevels.debug);
        logger.logData("Target State", targetState, Logger.LogLevels.debug);

        logger.logData("Target Position", targetState.servoPos, Logger.LogLevels.developer);
        logger.logData("Encoder Position", encPos, Logger.LogLevels.developer);
    }

    private void findState() {
        currentState = PosChecker.atAngularPos(encPos, targetState.encPos, DepositConstants.armPositionTolerance) ? targetState : State.Intermediate;

    }

}