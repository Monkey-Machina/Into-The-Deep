package org.firstinspires.ftc.teamcode.Debugging;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Drivetrain;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Intake;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.SampleDetector;

import java.util.ArrayList;

@TeleOp
public class IntakeOpMode extends OpMode {
    private final Hardware hardware = new Hardware();
    private Logger logger;
    private GamepadEx controller;

    private Intake intake;
    private Drivetrain drivetrain;

    private ArrayList<SampleDetector.SampleColor> colors = new ArrayList<>();


    @Override
    public void init() {
        hardware.init(hardwareMap);
        controller = new GamepadEx(gamepad1);
        logger = new Logger(telemetry, controller);


        intake = new Intake(hardware, logger, controller,true);
        intake.setTargetState(Intake.State.Stowed);

        colors.add(SampleDetector.SampleColor.blue);
        colors.add(SampleDetector.SampleColor.yellow);

        intake.setAcceptableColors(colors);

        drivetrain = new Drivetrain(hardware, controller, logger, false);
    }

    @Override
    public void loop() {
        hardware.clearCache();
        controller.readButtons();
        drivetrain.update();

        intake.update();

        if (controller.wasJustPressed(GamepadKeys.Button.X)) {
            intake.setTargetState(Intake.State.Stowed);
        } else if (controller.wasJustPressed(GamepadKeys.Button.B)) {

            if (intake.targetState == Intake.State.Deployed) {
                intake.setTargetState(Intake.State.Intaking);
            } else {
                intake.setTargetState(Intake.State.Deployed);
            }

        }

        drivetrain.command();
        intake.command(controller.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER), controller.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER));

        intake.log();
        logger.print();
    }
}
