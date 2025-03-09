package org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.AnalogServo;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;

public class Arm {

    public enum State {
        TransferPos(DepositConstants.armTransferPos),
        SpecIntakePos(DepositConstants.armSpecIntakePos),
        SpecDepositPos(DepositConstants.armSpecDepositPos),
        SampleDepositPos(DepositConstants.armSampleDepositPos),
        SamplePreDepositPos(DepositConstants.armSamplePreDeposit),
        Intermediate(0);

        public final double position;

        State(double servoPos) {
            this.position = servoPos;
        }
    }

    private final Logger logger;

    private final AnalogServo servo;

    public State currentState;
    private State targetState;

    public double encPos = 0.00;

    private double offset = 0.00;

    public Arm(Hardware hardware, Logger logger) {
        this.logger = logger;
        servo = new AnalogServo(hardware.armServo, hardware.armEnc, DepositConstants.armEncLowerBound, DepositConstants.armEncUpperBound);
    }

    public void update() {
        encPos = servo.getPos();
        findState();
    }

    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    public void command() {
        servo.setPos(targetState.position + offset);
    }

    public void log() {
        logger.logHeader("Arm");

        logger.logData("Current State", currentState, Logger.LogLevels.debug);
        logger.logData("Target State", targetState, Logger.LogLevels.debug);

        logger.logData("Target Position", targetState.position + offset, Logger.LogLevels.developer);
        logger.logData("Encoder Position", encPos, Logger.LogLevels.developer);
        logger.logData("Offset", offset, Logger.LogLevels.developer);
        logger.logData("Raw Set Position", servo.getCommandedPos(), Logger.LogLevels.developer);
    }

    public void changeOffset(double deltaOffset) {
        offset += deltaOffset;
    }

    private void findState() {
        currentState = PosChecker.atAngularPos(encPos, targetState.position + offset, DepositConstants.armPositionTolerance) ? targetState : State.Intermediate;
    }
}