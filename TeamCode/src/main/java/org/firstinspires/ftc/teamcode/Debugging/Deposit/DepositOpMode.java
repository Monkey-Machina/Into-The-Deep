package org.firstinspires.ftc.teamcode.Debugging.Deposit;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Deposit;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Drivetrain;
@Config
@TeleOp
public class DepositOpMode extends OpMode {
    private Hardware hardware = new Hardware();
    private Logger logger;
    private Deposit deposit;
    private Drivetrain drivetrain;
    private GamepadEx controller;


    @Override
    public void init() {
        hardware.init(hardwareMap);
        controller = new GamepadEx(gamepad1);
        logger = new Logger(telemetry, controller);
        deposit = new Deposit(hardware, logger);

        drivetrain = new Drivetrain(hardware, controller, logger, false);
        deposit.setTargetState(Deposit.State.transfer);
        deposit.setClaw(Claw.State.Open);
    }

    @Override
    public void loop() {
        hardware.clearCache();
        controller.readButtons();

        drivetrain.update();
        deposit.update();


        if (controller.wasJustPressed(GamepadKeys.Button.A)){
            deposit.setTargetState(Deposit.State.transfer);
        } else if (controller.wasJustPressed(GamepadKeys.Button.B)) {
            deposit.setTargetState(Deposit.State.specIntake);

        } else if (controller.wasJustPressed(GamepadKeys.Button.Y)) {
            deposit.setTargetState(Deposit.State.sampleDeposit);
        } else if (controller.wasJustPressed(GamepadKeys.Button.X)) {
            deposit.setTargetState(Deposit.State.specDeposit);
        }

        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            deposit.toggleClaw();
        }

        drivetrain.command();
        deposit.command();

        drivetrain.log();
        deposit.log();

        logger.print();
    }
}