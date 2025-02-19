package org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.AnalogServo;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;

public class Wrist {

    public enum State {
        TransferPos(DepositConstants.wristTransferPos),
        SpecIntakePos(DepositConstants.wristSpecIntakePos),
        SpecDepositPos(DepositConstants.wristSpecDepositPos),
        SampleDepositPos(DepositConstants.wristSampleDepositPos),
        Intermediate(0);

        public final double servoPos;

        State(double servoPos) {
            this.servoPos = servoPos;
        }
    }

    private final Logger logger;

    private final AnalogServo servo;

    public State currentState;
    private State targetState;

    public double encPos = 0.00;

    public double offset = 0.00;

    public Wrist(Hardware hardware, Logger logger) {
        this.logger = logger;
        servo = new AnalogServo(hardware.wristServo, hardware.wristEnc, DepositConstants.wristEncLowerBound, DepositConstants.wristEncUpperBound);
    }

    public void update() {
        encPos = servo.getPos();
        findState();
    }

    public void setTargetState(State targetState) {
        this.targetState = targetState;
    }

    public void command() {
        servo.setPos(targetState.servoPos + offset);
    }

    public void log() {
        logger.logHeader("Wrist");

        logger.logData("Current State", currentState, Logger.LogLevels.debug);
        logger.logData("Target State", targetState, Logger.LogLevels.debug);

        logger.logData("Target Position", targetState.servoPos + offset, Logger.LogLevels.developer);
        logger.logData("Encoder Position", encPos, Logger.LogLevels.developer);
        logger.logData("Offset", offset, Logger.LogLevels.developer);
        logger.logData("Raw Set Position", servo.getCommandedPos(), Logger.LogLevels.developer);
    }

    public void changeOffset(double deltaOffset) {
        offset += deltaOffset;
    }

    private void findState() {
        currentState = PosChecker.atAngularPos(encPos, targetState.servoPos + offset, DepositConstants.wristPositionTolerance) ? targetState : State.Intermediate;

    }
}