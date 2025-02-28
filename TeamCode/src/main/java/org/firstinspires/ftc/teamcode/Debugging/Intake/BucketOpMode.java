package org.firstinspires.ftc.teamcode.Debugging.Intake;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.Bucket;

@TeleOp
public class BucketOpMode extends OpMode {

    private final Hardware hardware =  new Hardware();
    private Logger logger;

    private Bucket bucket;
    private GamepadEx controller;

    @Override
    public void init() {
        hardware.init(hardwareMap);

        controller  = new GamepadEx(gamepad1);
        logger = new Logger(telemetry, controller);

        bucket = new Bucket(hardware, logger);
        bucket.setTargetStates(Bucket.BucketState.Up, Bucket.GateState.Open);
    }

    @Override
    public void loop() {
        hardware.clearCache();
        controller.readButtons();
        bucket.update();

        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            bucket.changeOffsets(5, 0.0);
        } else if (controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            bucket.changeOffsets(-5, 0.0);
        }

        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
            bucket.changeOffsets(0.0, 5);
        } else if (controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
            bucket.changeOffsets(0.0, -5);
        }

        if (controller.getButton(GamepadKeys.Button.RIGHT_BUMPER)) {
            bucket.setTargetStates(Bucket.BucketState.Down, Bucket.GateState.Closed);
            bucket.setRollerPower(IntakeConstants.intakingPower);
        } else {
            bucket.setTargetStates(Bucket.BucketState.Up, Bucket.GateState.Open);
            bucket.setRollerPower(0.0);
        }

        bucket.command();

        bucket.log();

        logger.print();
    }
}
