package org.firstinspires.ftc.teamcode.Debugging.Deposit;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Arm;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Wrist;

@TeleOp
public class DepositServoOpMode extends OpMode {

    private Hardware hardware = new Hardware();
    private Logger logger;
    private GamepadEx  controller;

    private Arm arm;
    private Wrist wrist;
    private Claw claw;

    private double offset = 0.00;
    private int servoIndex = 0;
    private String[] servoArray = {"Arm", "Wrist", "Claw"};

    @Override
    public void init() {
        hardware.init(hardwareMap);
        controller = new GamepadEx(gamepad1);
        logger = new Logger(telemetry, controller);

        arm = new Arm(hardware, logger);
        wrist = new Wrist(hardware, logger);
        claw = new Claw(hardware, logger);

        arm.setTargetState(Arm.State.TransferPos);
        wrist.setTargetState(Wrist.State.TransferPos);
        claw.setTargetState(Claw.State.Open);

    }

    @Override
    public void loop() {
        offset = 0.00;

        hardware.clearCache();
        controller.readButtons();

        arm.update();
        wrist.update();
        claw.update();

        servoIndex = controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT) ? (servoIndex + 1) % 3 : servoIndex;
        servoIndex = controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT) ? (servoIndex - 1) : servoIndex;
        servoIndex = servoIndex < 0 ? 3 + servoIndex : servoIndex;

        offset = controller.wasJustPressed(GamepadKeys.Button.DPAD_UP) ? offset + 5 : offset;
        offset = controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN) ? offset - 5 : offset;

        if (servoIndex == 0) {
            arm.changeOffset(offset);
        } else if (servoIndex == 1) {
            wrist.changeOffset(offset);
        } else if (servoIndex == 2) {
            claw.changeOffset(offset);
        }


        arm.command();
        wrist.command();
        claw.command();

        logger.logHeader("Deposit Servo Op-Mode");

        logger.logData("Current Servo", servoArray[servoIndex], Logger.LogLevels.production);

        arm.log();
        wrist.log();
        claw.log();

        logger.print();

        }

    }