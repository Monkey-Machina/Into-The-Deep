package org.firstinspires.ftc.teamcode.SystemsFSMs;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Hardware.Util.PosChecker;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Arm;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.DepositSlides;

public class Deposit {
    private Claw claw;
    // TODO: This being public is a jank fix for going to spec deposit position so that I can easily look at its position in the robot FSM
    public Arm arm;
    private DepositSlides slides;
    private Logger logger;
    private GamepadEx controller;

    public enum TargetState {
        transfer,
        preTransfer,
        specIntake,
        specDepositReady,
        specDepositClipped,
        sampleDeposit,
        samplePreDeposit,
        preSpecIntake,
        intermediate;
    }

    private TargetState targetState;
    private TargetState currentState;

    private boolean transfer = false;

    public Deposit(Hardware hardware, GamepadEx controller, Logger logger){
        this.logger = logger;
        this.controller = controller;

        claw = new Claw(hardware, logger);
        arm = new Arm(hardware, logger);
        slides = new DepositSlides(hardware, logger);

    }

    public void update() {
        claw.update();
        arm.update();
        slides.update();

        findState();
    }

    public void command() {

        switch (targetState) {
            case transfer:

                arm.setPosition(DepositConstants.armRightTransferPos);
                slides.setTargetCM(DepositConstants.slideTransferPos);

                break;

            case preTransfer:

                slides.setTargetCM(DepositConstants.slidePreTransferPos);
                arm.setPosition(DepositConstants.armRightTransferPos);

                break;

            case specIntake:

                    slides.setTargetCM(DepositConstants.slideSpecIntakePos);
                    arm.setPosition(DepositConstants.armRightSpecIntakePos);

                break;

            case preSpecIntake:
                    slides.setTargetCM(DepositConstants.slidePreTransferPos);
                    if (slides.getPosition() >= DepositConstants.slidePreTransferPos - DepositConstants.slidePositionTolerance) {
                        arm.setPosition(DepositConstants.armRightSampleDepositPos);
                    } else {
                        arm.setPosition(DepositConstants.armRightTransferPos);
                    }
                break;
            case specDepositReady:

                slides.setTargetCM(DepositConstants.slideSpecDepositReadyPos);
                arm.setPosition(DepositConstants.armRightSpecDepositPos);

                break;

            case specDepositClipped:

                slides.setTargetCM(DepositConstants.slideSpecClippedPos);
                arm.setPosition(DepositConstants.armRightSpecDepositPos);

                break;

            case sampleDeposit:

                slides.setTargetCM(DepositConstants.slideSampleDepositPos);
                arm.setPosition(DepositConstants.armRightSampleDepositPos);

                break;

            case samplePreDeposit:
                slides.setTargetCM(DepositConstants.slideSampleDepositPos);
                arm.setPosition(DepositConstants.armRightTransferPos);

                break;
        }


        // Toggle Claw
        if (controller.wasJustPressed(GamepadKeys.Button.RIGHT_BUMPER)) {

            if (claw.getTargetPosition() == DepositConstants.clawOpenPos) {
                claw.setTargetPosition(DepositConstants.clawClosedPos);
            } else if (claw.getTargetPosition() == DepositConstants.clawClosedPos) {
                claw.setTargetPosition(DepositConstants.clawOpenPos);
            }

        }



        claw.command();
        arm.command();
        slides.command();
    }

    public void log() {
        logger.logData("<b>" + "-Deposit-" + "</b>", "", Logger.LogLevels.production);

        logger.logData("Target State", targetState, Logger.LogLevels.production);
        logger.logData("Current State", currentState, Logger.LogLevels.production);

        claw.log();
        arm.log();
        slides.log();
    }

    public void setTargetState(TargetState state) {
        targetState = state;
    }

    public TargetState getCurrentState() {
        return currentState;
    }

    public TargetState getTargetState() {
        return targetState;
    }

    public void setTransfer(boolean value) {
        transfer = value;
    }

    private void findState() {

        if (arm.getStatus() == Arm.State.TransferPos && PosChecker.atLinearPos(slides.getPosition(), DepositConstants.slideTransferPos, DepositConstants.slidePositionTolerance) && slides.getTargetCM() == DepositConstants.slideTransferPos) {
            currentState = TargetState.transfer;
        } else if (arm.getStatus() == Arm.State.TransferPos && PosChecker.atLinearPos(slides.getPosition(), DepositConstants.slidePreTransferPos, DepositConstants.slidePositionTolerance) && slides.getTargetCM() == DepositConstants.slidePreTransferPos) {
            currentState = TargetState.preTransfer;
        } else if (arm.getStatus() == Arm.State.SpecIntakePos && PosChecker.atLinearPos(slides.getPosition(), DepositConstants.slideSpecIntakePos, DepositConstants.slidePositionTolerance) && slides.getTargetCM() == DepositConstants.slideSpecIntakePos) {
            currentState = TargetState.specIntake;
        } else if (arm.getStatus() == Arm.State.SpecDepositPos && PosChecker.atLinearPos(slides.getPosition(), DepositConstants.slideSpecDepositReadyPos, DepositConstants.slidePositionTolerance) && slides.getTargetCM() == DepositConstants.slideSpecDepositReadyPos) {
            currentState = TargetState.specDepositReady;
        } else if (arm.getStatus() == Arm.State.SpecDepositPos && PosChecker.atLinearPos(slides.getPosition(), DepositConstants.slideSpecClippedPos, DepositConstants.slidePositionTolerance) && slides.getTargetCM() == DepositConstants.slideSpecClippedPos) {
            currentState = TargetState.specDepositClipped;
        } else if (arm.getStatus() == Arm.State.SampleDepositPos && PosChecker.atLinearPos(slides.getPosition(), DepositConstants.slideSampleDepositPos, DepositConstants.slidePositionTolerance) && slides.getTargetCM() == DepositConstants.slideSampleDepositPos) {
            currentState = TargetState.sampleDeposit;
        } else {
            currentState =TargetState.intermediate;
        }

    }

    public void gripClaw() {
        claw.setTargetPosition(DepositConstants.clawClosedPos);
    }

    public void releaseClaw() {
        claw.setTargetPosition(DepositConstants.clawOpenPos);
    }

    public Claw.Status getClawStatus() {
        return claw.getStatus();
    }

    public double getSlideCurrentCM() {
        return slides.getPosition();
    }

    public Arm.State getArmStatus() {
        return arm.getStatus();
    }

    public boolean getSlidesDownSafe() {
        return arm.getSlideSafeDown();
    }

    public double getSlideTargetCM() {
        return slides.getTargetCM();
    }

    public void updateSlidesafe() {
        arm.updateSlideSafe();
    }

}
