package org.firstinspires.ftc.teamcode.Debugging.Deposit;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.DepositSlides;

@Config
@TeleOp
public class DepositSlideOpMode extends OpMode {
    private Hardware hardware = new Hardware();
    private DepositSlides slides;
    private Logger logger;
    private GamepadEx controller;

    public static double
            p = DepositConstants.sp,
            i = DepositConstants.si,
            d = DepositConstants.sd,
            f = DepositConstants.sf;

    @Override
    public void init() {
        hardware.init(hardwareMap);
        controller = new GamepadEx(gamepad1);
        logger = new Logger(telemetry, controller);
        slides = new DepositSlides(hardware, logger);

        slides.setTargetState(DepositSlides.State.TransferPos);
    }

    @Override
    public void loop() {
        hardware.clearCache();
        controller.readButtons();

        slides.update();

        slides.setPID(p,i, d, f);

        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            slides.setTargetState(DepositSlides.State.SampleDepositPos);
        } else if (controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            slides.setTargetState(DepositSlides.State.TransferPos);
        } else if (controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT)) {
            slides.setTargetState(DepositSlides.State.SpecDepositPos);
        } else if (controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
            slides.setTargetState(DepositSlides.State.SpecIntakePos);
        }

        slides.command();

        slides.log();
        logger.print();
    }

    private void callI2C() {
        hardware.pinPoint.update();
        hardware.intakeCS.updateColors();
    }

}
