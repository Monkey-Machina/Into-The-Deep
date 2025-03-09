package org.firstinspires.ftc.teamcode.SystemsFSMs;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.Hardware.Drivers.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;

public class Drivetrain {

    private Logger logger;


    private DcMotorEx LF;
    private DcMotorEx RF;
    private DcMotorEx RB;
    private DcMotorEx LB;

    private GoBildaPinpointDriver pinPoint;

    private double[] sticks = {0, 0, 0, 0};
    private double heading = 0;

    private Pose2D position;
    private boolean readPos = false;

    GamepadEx gamepad;

    private double LFPower = 0.00;
    private double RFPower = 0.00;
    private double RBPower = 0.00;
    private double LBPower = 0.00;

    // TODO: Motor mappings are wrong, so those need to be fixed, and this code needs to be updated
    public Drivetrain(Hardware hardware, GamepadEx gamepad, Logger logger, boolean readPos) {
        this.logger = logger;

        LF = hardware.LF;
        RF = hardware.RF;
        RB = hardware.RB;
        LB = hardware.LB;

        pinPoint = hardware.pinPoint;
        this.gamepad = gamepad;

        this.readPos = readPos;
    }

    public void update() {
        if (readPos) {
            pinPoint.update();
        } else {
            pinPoint.update(GoBildaPinpointDriver.readData.ONLY_UPDATE_HEADING);
        }

        heading = -pinPoint.getHeading();
        position = pinPoint.getPosition();

        sticks[0] = gamepad.getRightX() * 0.75;
        sticks[1] = gamepad.getRightY();
        sticks[2] = gamepad.getLeftX();
        sticks[3] = gamepad.getLeftY();
    }

    public void command() {

        double rotX = sticks[2] * Math.cos(heading) - sticks[3] * Math.sin(heading);
        double rotY = sticks[2] * Math.sin(heading) + sticks[3] * Math.cos(heading);

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(sticks[0]), 1);
        LFPower = (rotY + rotX + sticks[0]) / denominator;
        RFPower = (rotY - rotX - sticks[0]) / denominator;
        RBPower = (rotY + rotX - sticks[0]) / denominator;
        LBPower = (rotY - rotX + sticks[0]) / denominator;

        LF.setPower(LFPower);
        RF.setPower(RFPower);
        RB.setPower(RBPower);
        LB.setPower(LBPower);
    }

    public void log() {
        logger.logData("<b>" + "-Drivetrain-" + "</b>", "", Logger.LogLevels.production);

        logger.logData("Heading", heading, Logger.LogLevels.debug);
        logger.logData("Position", position, Logger.LogLevels.debug);

        logger.logData("LF Power", LFPower, Logger.LogLevels.developer);
        logger.logData("RF Power", RFPower, Logger.LogLevels.developer);
        logger.logData("RB Power", RBPower, Logger.LogLevels.developer);
        logger.logData("LB Power", LBPower, Logger.LogLevels.developer);


    }

    public Pose2D getPos2D() {
        return  position;
    }

}
