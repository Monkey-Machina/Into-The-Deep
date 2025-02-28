//TODO: Refactor

//package org.firstinspires.ftc.teamcode;
//
//import com.arcrobotics.ftclib.gamepad.GamepadEx;
//import com.arcrobotics.ftclib.gamepad.GamepadKeys;
//import com.qualcomm.robotcore.eventloop.opmode.OpMode;
//
//import org.firstinspires.ftc.teamcode.Hardware.Hardware;
//import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
//import org.firstinspires.ftc.teamcode.SystemsFSMs.Deposit;
//import org.firstinspires.ftc.teamcode.SystemsFSMs.Intake;
//import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.SampleDetector;
//import org.firstinspires.ftc.teamcode.SystemsFSMs.Robot;
//
//import java.util.ArrayList;
//
//@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name = "Tele-Op V1.0.0", group = "Competition")
//public class TeleOp extends OpMode {
//    private Hardware hardware = new Hardware();
//    private Logger logger;
//    private GamepadEx controller;
//
//    private Robot robot;
//
//    private boolean blueAlliance = true;
//    private boolean redAlliance = false;
//
//    private boolean fastestStrategy = true;
//    private boolean specStrategy = false;
//    private boolean sampleStrategy = false;
//
//    private boolean leftTriggerPressed = false;
//    @Override
//    public void init() {
//        hardware.init(hardwareMap);
//        controller = new GamepadEx(gamepad1);
//        logger = new Logger(telemetry, controller);
//
//        hardware.Zero();
//
//        robot  = new Robot(hardware, controller, logger);
//
//        robot.setDepositDesiredState(Deposit.State.transfer);
//        robot.setIntakeDesiredState(Intake.SystemState.Stowed);
//    }
//
//    @Override
//    public void init_loop() {
//        updateSamples();
//        logger.print();
//    }
//
//    @Override
//    public void loop() {
//        hardware.clearCache();
//        controller.readButtons();
//
//        updateSamples();
//
//        robot.update();
//
//        // Zero Pinpoint
//        if (controller.wasJustPressed(GamepadKeys.Button.START)) {
//            hardware.zeroPinpoint();
//        }
//
//
//        // Deposit Controls
//        // Send deposit to transfer position |A|
//        if (controller.wasJustPressed(GamepadKeys.Button.A)) {
//
//            robot.setDepositDesiredState(Deposit.State.transfer);
//            robot.releaseClaw();
//        }
//
//        // Go to deposit |Y|
//        if (controller.wasJustPressed(GamepadKeys.Button.Y)) {
//
//            robot.goToDeposit();
//
//        }
//

//
//        // Switching the last seen color manually
//        if (controller.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) >= 0.2 && !leftTriggerPressed) {
//            leftTriggerPressed = true;
//            robot.switchColor();
//
//            robot.goToDeposit();
//        } else if (controller.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER) == 0.00) {
//            leftTriggerPressed = false;
//        }
//
//        robot.command();
//        robot.log();
//
//        logger.print();
//    }
//
//    private void updateAlliance() {
//        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_LEFT) || controller.wasJustPressed(GamepadKeys.Button.DPAD_RIGHT)) {
//            redAlliance = !redAlliance;
//            blueAlliance = !blueAlliance;
//        }
//    }
//
//    private void updateStrategy() {
//
//        // D Pad down strategy controls | fastest -> spec -> sample -> fastest
//        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
//
//            if (fastestStrategy) {
//                fastestStrategy = false;
//                specStrategy = true;
//                sampleStrategy = false;
//            } else if (specStrategy) {
//                fastestStrategy = false;
//                specStrategy = false;
//                sampleStrategy = true;
//            } else  if (sampleStrategy) {
//                fastestStrategy = true;
//                specStrategy = false;
//                sampleStrategy = false;
//            }
//        }
//
//        // D Pad up strategy controls | fastest -> sample -> spec -> fastest
//        if (controller.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
//
//            if (fastestStrategy) {
//                fastestStrategy = false;
//                specStrategy = false;
//                sampleStrategy = true;
//            } else if (specStrategy) {
//                fastestStrategy = true;
//                specStrategy = false;
//                sampleStrategy = false;
//            } else if (sampleStrategy) {
//                fastestStrategy = false;
//                specStrategy = true;
//                sampleStrategy = false;
//            }
//
//
//        }
//
//    }
//
//    private void updateSamples() {
//
//        updateAlliance();
//        updateStrategy();
//
//        ArrayList<SampleDetector.SampleColor> colors = new ArrayList<>();
//
//        if (blueAlliance) {
//
//            logger.logData("<b>" + "Blue Alliance" + "</b>", "", Logger.LogLevels.production);
//
//            if (fastestStrategy) {
//                logger.logData("<b>" + "Fastest Strategy" + "</b>", "", Logger.LogLevels.production);
//                colors.add(SampleDetector.SampleColor.blue);
//                colors.add(SampleDetector.SampleColor.yellow);
//            }
//
//            if (specStrategy) {
//                logger.logData("<b>" + "Spec Strategy" + "</b>", "", Logger.LogLevels.production);
//                colors.add(SampleDetector.SampleColor.blue);
//            }
//
//            if (sampleStrategy) {
//                logger.logData("<b>" + "Sample Strategy" + "</b>", "", Logger.LogLevels.production);
//                colors.add(SampleDetector.SampleColor.yellow);
//            }
//        }
//
//        if (redAlliance) {
//
//            logger.logData("<b>" + "Red Alliance" + "</b>", "", Logger.LogLevels.production);
//
//            if (fastestStrategy) {
//                logger.logData("<b>" + "Fastest Strategy" + "</b>", "", Logger.LogLevels.production);
//                colors.add(SampleDetector.SampleColor.red);
//                colors.add(SampleDetector.SampleColor.yellow);
//            }
//
//            if (specStrategy) {
//                logger.logData("<b>" + "Spec Strategy" + "</b>", "", Logger.LogLevels.production);
//                colors.add(SampleDetector.SampleColor.red);
//            }
//
//            if (sampleStrategy) {
//                logger.logData("<b>" + "Sample Strategy" + "</b>", "", Logger.LogLevels.production);
//                colors.add(SampleDetector.SampleColor.yellow);
//            }
//
//        }
//
//        robot.setAcceptedSamples(colors);
//
//    }
//}