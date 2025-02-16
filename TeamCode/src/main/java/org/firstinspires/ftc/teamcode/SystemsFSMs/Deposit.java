package org.firstinspires.ftc.teamcode.SystemsFSMs;

import com.arcrobotics.ftclib.gamepad.GamepadEx;

import org.firstinspires.ftc.teamcode.Hardware.Constants.DepositConstants;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Arm;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.DepositSlides;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Wrist;

public class Deposit {

    private Arm arm;
    private Wrist wrist;
    private Claw claw;

    private DepositSlides slides;

    private Logger logger;
    private GamepadEx controller;

    public enum State {

        transfer(Arm.State.TransferPos, Wrist.State.TransferPos, DepositSlides.State.TransferPos),
        specIntake(Arm.State.SpecIntakePos, Wrist.State.SpecIntakePos, DepositSlides.State.SpecIntakePos),
        specDeposit(Arm.State.SpecDepositPos, Wrist.State.SpecDepositPos, DepositSlides.State.SpecDepositPos),
        sampleDeposit(Arm.State.SampleDepositPos, Wrist.State.SampleDepositPos, DepositSlides.State.SampleDepositPos),
        intermediate(Arm.State.Intermediate, Wrist.State.Intermediate, DepositSlides.State.Intermediate);

        private final Arm.State armState;
        private final Wrist.State wristState;
        private final DepositSlides.State slideState;

        State(Arm.State armState, Wrist.State wristState, DepositSlides.State slideState) {
            this.armState = armState;
            this.wristState = wristState;
            this.slideState = slideState;
        }
    }

    private State targetState;
    private State currentState;

    public Deposit(Hardware hardware, GamepadEx controller, Logger logger){
        this.logger = logger;
        this.controller = controller;

        arm = new Arm(hardware, logger);
        wrist = new Wrist(hardware, logger);
        claw = new Claw(hardware, logger);
        slides = new DepositSlides(hardware, logger);

    }

    public void update() {
        arm.update();
        wrist.update();
        claw.update();
        slides.update();

        findState();
    }

    public void command() {

        arm.setTargetState(targetState.armState);
        wrist.setTargetState(targetState.wristState);
        slides.setTargetState(targetState.slideState);

        arm.command();
        wrist.command();
        claw.command();
        slides.command();
    }

    //TODO: Refactor
    public void log() {
        logger.logHeader("--DEPOSIT--");

        logger.logData("Target State", targetState, Logger.LogLevels.production);
        logger.logData("Current State", currentState, Logger.LogLevels.production);

        claw.log();
        arm.log();
        slides.log();
    }

    public void setTargetState(State state) {
        targetState = state;
    }

    public void setClaw(Claw.State targetState) {
        claw.setTargetState(targetState);
    }

    public void toggleClaw() {
        Claw.State targetState = claw.targetState == Claw.State.Open ? Claw.State.Closed : Claw.State.Open;
        claw.setTargetState(targetState);
    }

    private void findState() {
        currentState = arm.currentState == targetState.armState && wrist.currentState == targetState.wristState && slides.currentState == targetState.slideState ? targetState : State.intermediate;
    }

}
