package org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.AnalogServo;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;

public class Claw {
    private AnalogServo servo;
    private Logger logger;

    public enum Status {
        released,
        gripped,
        intermediate;
    }

    private Status status;

    private double targetPosition = DepositConstants.clawOpenPos;
    private double encoderPos = 0.00;

    public Claw(Hardware hardware, Logger logger) {
        servo = new AnalogServo(hardware.claw, hardware.clawEnc);
        this.logger = logger;
    }

    public void update() {
        encoderPos = servo.getPos();
        findStatus();
    }

    public void command() {
        servo.setPos(targetPosition);
    }

    public void log() {
        logger.logData("<b>" + "Claw" + "</b>", "", Logger.LogLevels.production);

        logger.logData("Status", status, Logger.LogLevels.debug);

        logger.logData("Target Position", targetPosition, Logger.LogLevels.developer);
        logger.logData("Encoder Position", encoderPos, Logger.LogLevels.developer);
    }

    public void setTargetPosition(double position) {
        targetPosition = position;
    }

    public double getTargetPosition() {
        return targetPosition;
    }

    public Status getStatus() {
        return status;
    }

    private void findStatus() {
        if (encoderPos <= DepositConstants.clawEncOpenPos + DepositConstants.clawEncPosTolerance) {
            status = Status.released;
        } else if (encoderPos >= DepositConstants.clawClosedPos - DepositConstants.clawEncPosTolerance) {
            status = Status.gripped;
        } else {
            status = Status.intermediate;
        }
    }

}
