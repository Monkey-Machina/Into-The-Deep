package org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;


import org.firstinspires.ftc.teamcode.Hardware.Util.AnalogServo;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Arm;

public class Bucket {

    private DcMotorEx rollerMotor;
    private AnalogServo bucketServo;
    private AnalogServo gateServo;
    private Logger logger;

    private double bucketServoEncPosition = 0.0;
    private double gateServoEncPosition = 0.0;


    private double rollerPower;
    private double current = 0.00;

    public enum BucketState {
        Up(IntakeConstants.bucketUpPosition),
        Down(IntakeConstants.bucketDownPosition),
        Intermediate(0.0);

        public final double position;

        BucketState(double position) {
            this.position = position;
        }
    }

    public enum GateState {
        Open(IntakeConstants.gateOpenPosition),
        Closed(IntakeConstants.gateClosedPosition),
        Compressed(IntakeConstants.gateCompressedPosition),
        Intermediate(0.0);

        public final double position;

        GateState(double position) {
            this.position = position;
        }
    }

    public BucketState bucketCurrentState;
    public GateState gateCurrentState;

    private BucketState bucketTargetState = BucketState.Up;
    private GateState gateTargetState = GateState.Closed;

    public double bucketOffset = 0.0, gateOffset = 0.0;


    public Bucket (Hardware hardware, Logger logger) {
        this.logger = logger;

        rollerMotor = hardware.intakeRoller;
        bucketServo = new AnalogServo(hardware.intakePivot, hardware.intakePivotEnc, IntakeConstants.bucketEncLowerBound, IntakeConstants.bucketEncUpperBound);
        gateServo = new AnalogServo(hardware.intakeGate, hardware.intakeGateEnc, IntakeConstants.gateEncLowerBound, IntakeConstants.gateEncUpperBound);
    }

    public void update() {
        bucketServoEncPosition = bucketServo.getPos();
        gateServoEncPosition = gateServo.getPos();

        current = rollerMotor.getCurrent(CurrentUnit.MILLIAMPS);
        
        findState();
    }

    public void setTargetStates(BucketState bucketTargetState, GateState gateTargetState) {
        this.bucketTargetState = bucketTargetState;
        this.gateTargetState = gateTargetState;
    }

    public void setBucketTargetState(BucketState bucketTargetState) {
        setTargetStates(bucketTargetState, this.gateTargetState);
    }

    public void setGateTargetState(GateState gateTargetState) {
        setTargetStates(this.bucketTargetState, gateTargetState);
    }

    public void setRollerPower(double power) {
        rollerPower = power;
    }

    public void command() {
        bucketServo.setPos(bucketTargetState.position + bucketOffset);
        gateServo.setPos(gateTargetState.position + gateOffset);
        rollerMotor.setPower(rollerPower);
    }

    public void log() {
        logger.logHeader("Bucket");

        logger.logData("Bucket Current State", bucketCurrentState, Logger.LogLevels.debug);
        logger.logData("Bucket Target State", bucketTargetState, Logger.LogLevels.debug);

        logger.logData("Bucket Servo Target Pos", bucketTargetState.position + bucketOffset, Logger.LogLevels.developer);
        logger.logData("Bucket Servo Pos", bucketServoEncPosition, Logger.LogLevels.developer);

        logger.logData("Gate Current State", gateCurrentState, Logger.LogLevels.debug);
        logger.logData("Gate Target State", gateTargetState, Logger.LogLevels.debug);

        logger.logData("Gate Servo Target Pos", gateTargetState.position + gateOffset, Logger.LogLevels.developer);
        logger.logData("Gate Servo Pos", gateServoEncPosition, Logger.LogLevels.developer);

        logger.logData("Roller Power", rollerPower, Logger.LogLevels.developer);
        logger.logData("Roller Current", current, Logger.LogLevels.developer);
    }

    public void changeOffsets(double deltaBucketOffset, double deltaGateOffset) {
        bucketOffset += deltaBucketOffset;
        gateOffset += deltaGateOffset;
    }

    private void findState() {
        bucketCurrentState = PosChecker.atAngularPos(bucketServoEncPosition, bucketTargetState.position + bucketOffset, IntakeConstants.bucketEncPositionTolerance) ? bucketTargetState : BucketState.Intermediate;
        gateCurrentState = PosChecker.atAngularPos(gateServoEncPosition, gateTargetState.position + gateOffset, IntakeConstants.gateEncPositionTolerance) ? gateTargetState : GateState.Intermediate;
    }
}