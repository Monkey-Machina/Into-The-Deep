//TODO: Refactor

package org.firstinspires.ftc.teamcode.SystemsFSMs;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.util.Timing;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.Bucket;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Mechaisms.SampleDetector;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Robot {

    private Drivetrain drivetrain;
    public Deposit deposit;
    private Intake intake;

    private GamepadEx controller;
    private Logger logger;

    public enum States {
        cycling,
        handoff;
    }

    public enum Strategy {
        split,
        basket;
    }

    private Strategy strategy = Strategy.split;

    private States currentState;
    public Deposit.State depositDesiredState;
    public Intake.State intakeDesiredState;

    private boolean interference;
    private boolean clawSetForTransfer;

    private Timing.Timer passthroughTimer = new Timing.Timer(100000000, TimeUnit.MILLISECONDS);
    private boolean passthroughReady = false;

    public Robot(Hardware hardware, GamepadEx controller, Logger logger, boolean intakeZeroing, boolean odometryEnabled) {

        this.controller = controller;
        this.logger = logger;

        drivetrain = new Drivetrain(hardware, controller, logger, odometryEnabled);
        deposit = new Deposit(hardware, logger);
        intake = new Intake(hardware, logger, controller, intakeZeroing);

        deposit.setTargetState(Deposit.State.transfer);
        intake.setTargetState(Intake.State.Stowed);

        findState();
    }

    public void update() {
        drivetrain.update();
        deposit.update();
        intake.update();
        findState();
    }

    public void command() {
        interferenceCheck();
        switch (currentState) {
            case cycling:

                cycleIntakeLogic();
                cycleDepositLogic();

                passthroughReady = false;


                break;

            case handoff:

                if (strategy == Strategy.basket || intake.lastSeenColor == SampleDetector.SampleColor.yellow) {
                    transferLogic();
                } else {
                    passthroughLogic();
                }

                break;

        }

        drivetrain.command();
        deposit.command();
        intake.command(controller.getTrigger(GamepadKeys.Trigger.LEFT_TRIGGER), controller.getTrigger(GamepadKeys.Trigger.RIGHT_TRIGGER));

    }

    public void log() {
        logger.logHeader("--Robot--");

        logger.logData("Strategy", strategy, Logger.LogLevels.production);

        logger.logData("Current State", currentState, Logger.LogLevels.production);
        logger.logData("Deposit Desired State", depositDesiredState, Logger.LogLevels.production);
        logger.logData("Intake Desired State", intakeDesiredState, Logger.LogLevels.production);

        logger.logData("Interference", interference, Logger.LogLevels.debug);

        logger.logData("Passthough Timer", passthroughTimer.elapsedTime(), Logger.LogLevels.developer);
        logger.logData("Passthough Ready", passthroughReady, Logger.LogLevels.developer);


        drivetrain.log();
        deposit.log();
        intake.log();
    }

    public void setDepositDesiredState(Deposit.State state) {
        depositDesiredState = state;
    }

    public void setIntakeDesiredState(Intake.State state) {
        intakeDesiredState = state;
    }

    public void goToDeposit() {

        if(strategy == Strategy.basket || intake.lastSeenColor == SampleDetector.SampleColor.yellow) {
            setDepositDesiredState(Deposit.State.sampleDeposit);
        // If the sample is known or red or blue, and the strategy is split, do specimens
        } else {
            if (deposit.targetState != Deposit.State.specIntake) {
                setDepositDesiredState(Deposit.State.specIntake);
            } else {
                setDepositDesiredState(Deposit.State.specDeposit);
            }
        }
    }

    public void setAcceptedSamples(ArrayList<SampleDetector.SampleColor> colors) {
        intake.setAcceptableColors(colors);
    }

    public void setStrategy(Robot.Strategy strategy) {
        this.strategy = strategy;
    }

    public void switchColor() {
        if (intake.lastSeenColor == SampleDetector.SampleColor.yellow) {
            intake.setLastSeenColor(SampleDetector.SampleColor.blue);
        } else {
            intake.setLastSeenColor(SampleDetector.SampleColor.yellow);
        }
    }

    private void findState() {

        if (intake.currentState == Intake.State.Stowed && (deposit.currentState == Deposit.State.transfer || deposit.currentState == Deposit.State.specIntake) && intake.hasSample) {
            currentState = States.handoff;

        } else {
            currentState = States.cycling;
        }

    }

    private void interferenceCheck() {
        interference = false;

        // If the intake is stowed or trying to stow, apply stow interference, then apply interference
        if (intake.currentState == Intake.State.Stowed || intake.targetState == Intake.State.Stowed) {
            // If the arm is below the pre transfer spot and the slides are around the transfer height, and the arm wants to go to sample deposit pos, there is interference
            if (deposit.slides.currentCM <= DepositConstants.slideTransferPos + DepositConstants.slidePositionTolerance && deposit.arm.encPos > DepositConstants.armSamplePreDeposit - DepositConstants.armPositionTolerance && depositDesiredState == Deposit.State.sampleDeposit) {
                interference = true;
            }

        }

    }

    private void cycleIntakeLogic () {

        // If the intake was deployed, start intaking, if it was intaking, go back to deployed
        if (controller.wasJustPressed(GamepadKeys.Button.B)) {
            if (intake.targetState == Intake.State.Deployed) {
                intake.setTargetState(Intake.State.Intaking);
            } else {
                intake.setTargetState(Intake.State.Deployed);
            }
        }

        // If x pressed, stow intake
        if (controller.wasJustPressed(GamepadKeys.Button.X)) {
            intake.setTargetState(Intake.State.Stowed);
        }

    }

    private void cycleDepositLogic() {
        // If there is no interference potential, then all actions can be performed optimally for deposit
        if (!interference) {
            deposit.setTargetState(depositDesiredState);
        } else {

                    if (depositDesiredState == Deposit.State.sampleDeposit) {
                        deposit.setTargetState(Deposit.State.samplePreDeposit);
                    }

        }
    }

    private void transferLogic() {

        intake.setPassingThrough(false);

        // TODO: This logic sets the transfer behind by a loop if the claw is already open before transfer begins
        deposit.setTargetState(Deposit.State.transfer);
        intake.setTargetState(Intake.State.Stowed);

        if (clawSetForTransfer) {
            deposit.setClaw(Claw.State.Closed);
            if (deposit.claw.currentState == Claw.State.Closed) {
                intake.hasSample = false;
                clawSetForTransfer = false;
                controller.gamepad.rumble(1.0, 1.0, 200);
            }
        } else {
            deposit.setClaw(Claw.State.Open);
            if (deposit.claw.currentState == Claw.State.Open) {
                clawSetForTransfer = true;
            }

        }
    }

    //TODO: this just like, doesnt work lmao
    private void passthroughLogic() {

        intake.setPassingThrough(true);
        if (intake.currentState == Intake.State.Stowed && intake.bucket.gateCurrentState == Bucket.GateState.Compressed & !passthroughReady) {
            passthroughReady = true;
            passthroughTimer.start();
        }

        if (passthroughReady) {
            intake.setPassthroughEject(true);
            if (passthroughTimer.elapsedTime() >= 1000) {
                intake.setPassthroughEject(false);
                passthroughTimer.pause();
                passthroughReady = false;
                intake.hasSample = false;

            }
        }

    }

}
