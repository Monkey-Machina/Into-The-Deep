package org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.AnalogServo;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;

public class Claw {

    public enum State {
        Open(DepositConstants.clawOpenPos),
        Closed(DepositConstants.clawClosedPos),
        Intermediate(0);

        public final double position;

        State(double servoPos) {
            this.position = servoPos;
        }
    }

    private Logger logger;

    private AnalogServo servo;

    public State currentState;
    public State targetState;

    private double encPos = 0.00;

    private double offset;

    public Claw(Hardware hardware, Logger logger) {
        servo = new AnalogServo(hardware.clawServo, hardware.clawEnc, DepositConstants.clawEncLowerBound, DepositConstants.clawEncUpperBound);
        this.logger = logger;
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
        logger.logHeader("Claw");

        logger.logData("Current State", currentState, Logger.LogLevels.debug);
        logger.logData("Target State", targetState, Logger.LogLevels.debug);

        logger.logData("Target Position", targetState.position + offset, Logger.LogLevels.developer);
        logger.logData("Encoder Position", encPos, Logger.LogLevels.developer);
        logger.logData("Offset", offset, Logger.LogLevels.developer);
    }

    public void changeOffset(double deltaOffset) {
        offset += deltaOffset;
    }

    private void findState() {
        currentState = PosChecker.atAngularPos(encPos, targetState.position + offset, DepositConstants.clawEncPosTolerance) ? targetState : State.Intermediate;
    }

}