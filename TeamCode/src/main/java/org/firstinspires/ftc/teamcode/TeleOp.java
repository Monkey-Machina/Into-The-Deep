//TODO: Refactor

package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Deposit;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Intake;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.SampleDetector;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Robot;

import java.util.ArrayList;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Tele-Op V1.0.0", group = "Competition")
public class TeleOp extends OpMode {
    private final Hardware hardware = new Hardware();
    private Logger logger;
    private GamepadEx controller;

    private Robot robot;

    private boolean blueAlliance = true;
    private boolean redAlliance = false;

    private boolean fastestStrategy = true;
    private boolean specStrategy = false;
    private boolean sampleStrategy = false;


    @Override
    public void init() {
        hardware.init(hardwareMap, false);
        controller = new GamepadEx(gamepad1);
        logger = new Logger(telemetry, controller);

        hardware.Zero();

        robot = new Robot(hardware, controller, logger, true, true);

        robot.setDepositDesiredState(Deposit.State.transfer);
        robot.setIntakeDesiredState(Intake.State.Stowed);
    }

    @Override
    public void init_loop() {
        updateSamples();
        logger.print();
    }

    @Override
    public void loop() {
        hardware.clearCache();
        controller.readButtons();

        updateSamples();

        robot.update();

        // Zero Pinpoint
        if (controller.wasJustPressed(GamepadKeys.Button.START)) {
            hardware.zeroPinpoint();
        }


        // Deposit Controls
        // Send deposit to transfer position |A|
        if (controller.wasJustPressed(GamepadKeys.Button.A)) {

            robot.setDepositDesiredState(Deposit.State.transfer);
            robot.deposit.setClaw(Claw.State.Open);
        }

        // Go to deposit |Y|
        if (controller.wasJustPressed(GamepadKeys.Button.Y)) {

            robot.goToDeposit();

        }

        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {
            robot.deposit.toggleClaw();
        }


        // Switching the last seen color manually
        if (controller.wasJustPressed(GamepadKeys.Button.LEFT_BUMPER)) {

            robot.switchColor();
            robot.goToDeposit();

        }


            robot.command();
            robot.log();

            logger.print();
        }

        //TODO: probably also bad
        private void updateAlliance() {
            if (controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT) || controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
                redAlliance = !redAlliance;
                blueAlliance = !blueAlliance;
            }
        }

        //TODO: this hurts
        private void updateStrategy() {

            // D Pad down strategy controls | fastest -> spec -> sample -> fastest
            if (controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {

                if (fastestStrategy) {
                    fastestStrategy = false;
                    specStrategy = true;
                    sampleStrategy = false;
                } else if (specStrategy) {
                    fastestStrategy = false;
                    specStrategy = false;
                    sampleStrategy = true;
                } else if (sampleStrategy) {
                    fastestStrategy = true;
                    specStrategy = false;
                    sampleStrategy = false;
                }
            }

            // D Pad up strategy controls | fastest -> sample -> spec -> fastest
            if (controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {

                if (fastestStrategy) {
                    fastestStrategy = false;
                    specStrategy = false;
                    sampleStrategy = true;
                } else if (specStrategy) {
                    fastestStrategy = true;
                    specStrategy = false;
                    sampleStrategy = false;
                } else if (sampleStrategy) {
                    fastestStrategy = false;
                    specStrategy = true;
                    sampleStrategy = false;
                }


            }

        }

        //TODO: We are going to ignore this atrocity on man kind
        private void updateSamples() {

            updateAlliance();
            updateStrategy();

            ArrayList<SampleDetector.SampleColor> colors = new ArrayList<>();

            if (blueAlliance) {

                logger.logHeader("Blue Alliance");

                if (fastestStrategy) {
                    logger.logHeader("Fastest Strategy");
                    colors.add(SampleDetector.SampleColor.blue);
                    colors.add(SampleDetector.SampleColor.yellow);
                    robot.setStrategy(Robot.Strategy.split);
                }

                if (specStrategy) {
                    logger.logHeader("Spec Strategy");
                    colors.add(SampleDetector.SampleColor.blue);
                    robot.setStrategy(Robot.Strategy.split);
                }

                if (sampleStrategy) {
                    logger.logHeader("Sample Strategy");
                    colors.add(SampleDetector.SampleColor.yellow);
                    colors.add(SampleDetector.SampleColor.blue);
                    robot.setStrategy(Robot.Strategy.basket);
                }
            }

            if (redAlliance) {

                logger.logHeader("Red Alliance");

                if (fastestStrategy) {
                    logger.logHeader("Fastest Strategy");
                    colors.add(SampleDetector.SampleColor.red);
                    colors.add(SampleDetector.SampleColor.yellow);
                    robot.setStrategy(Robot.Strategy.split);
                }

                if (specStrategy) {
                    logger.logHeader("Spec Strategy");
                    colors.add(SampleDetector.SampleColor.red);
                    robot.setStrategy(Robot.Strategy.split);
                }

                if (sampleStrategy) {
                    logger.logHeader("Sample Strategy");
                    colors.add(SampleDetector.SampleColor.yellow);
                    colors.add(SampleDetector.SampleColor.red);
                    robot.setStrategy(Robot.Strategy.basket);
                }

            }

            robot.setAcceptedSamples(colors);

        }

    }