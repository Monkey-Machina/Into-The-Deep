//TODO: Refactor

package org.firstinspires.ftc.teamcode;


import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.pedropathing.follower.Follower;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.AutoPaths.Auto_4_0_Paths;
import org.firstinspires.ftc.teamcode.Hardware.Hardware;
import org.firstinspires.ftc.teamcode.Hardware.Util.Logger;
import org.firstinspires.ftc.teamcode.Pedro.Constants.FConstants;
import org.firstinspires.ftc.teamcode.Pedro.Constants.LConstants;
import org.firstinspires.ftc.teamcode.SystemsFSMs.Deposit;
import org.firstinspires.ftc.teamcode.SystemsFSMs.DepositLowLevel.Claw;

@Autonomous(name = "Auto V0.1.1", group = "Competition")
public class Auto_4_0 extends OpMode {

    private Logger logger;
    private Hardware hardware;
    private Deposit deposit;
    private GamepadEx controller;

    private Follower follower;

    private enum AutoState{
        start,
        specDepoOne,
        intakingSpecOne,
        specDepoTwo,
        samplePushOne,
        samplePushTwo,
        intakingSpecTwo,
        depositingSpecThree,
        intakingSpecThree,
        depositingSpecFour,
        park,
        done;
    }
    private AutoState autoState = AutoState.start;

    private enum SpecIntakingStatus {
        aligning,
        intaking
    }
    private SpecIntakingStatus specIntakingStatus = SpecIntakingStatus.aligning;

    private enum SpecDepoStatus {
        driving,
        releasing;
    }
    private SpecDepoStatus specDepoStatus = SpecDepoStatus.driving;

    private PathChain specDepoOnePC, specIntakeOnePC, specDepoTwoPC, samplePushOnePC, samplePushTwoPC, specIntakeTwoPC, specDepoThreePC, specIntakeThreePC, specDepoFourPC, parkPC;

    @Override
    public void init() {
        // Robot Setup
        hardware = new Hardware();
        hardware.init(hardwareMap, true, true);

        controller = new GamepadEx(gamepad1);

        logger = new Logger(telemetry, controller);

        deposit = new Deposit(hardware, logger);

        deposit.setTargetState(Deposit.State.specIntake);
        deposit.setClaw(Claw.State.Open);

        // Pedro & Path Setup
        Auto_4_0_Paths.build();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);

        follower.setStartingPose(Auto_4_0_Paths.startPose);

        buildPaths();
    }

    @Override
    public void init_loop() {
        hardware.clearCache();
        controller.readButtons();

        deposit.update();
        deposit.command();

        logger.logHeader("Auto Ready -- Init Loop");
        deposit.log();

        logger.print();
    }

    @Override
    public void loop() {
        hardware.clearCache();
        autoStateUpdate();
        deposit.update();
        controller.readButtons();

        follower.update();
        deposit.command();

        logger.logHeader("Auto");
        logger.logData("Auto State", autoState, Logger.LogLevels.production);
        logger.logData("Robot Pose", follower.getPose(), Logger.LogLevels.production);
        logger.logData("Follower Is Busy", follower.isBusy(), Logger.LogLevels.production);
        logger.logData("T-Value", follower.getCurrentTValue(), Logger.LogLevels.production);
        deposit.log();

        logger.print();
    }

    public void autoStateUpdate(){
        switch (autoState) {
            case start:
                deposit.setClaw(Claw.State.Closed);
                if (deposit.claw.currentState == Claw.State.Closed) {
                    deposit.setTargetState(Deposit.State.specDeposit);
                    follower.followPath(specDepoOnePC, true);
                    autoState = AutoState.specDepoOne;
                }
                break;

            case specDepoOne:
                depositSpec(specIntakeOnePC, AutoState.intakingSpecOne);
                break;

            case intakingSpecOne:
                intakeSpec(specDepoTwoPC, AutoState.specDepoTwo, 0.7, 0.4);
                break;

            case specDepoTwo:
                depositSpec(samplePushOnePC, AutoState.samplePushOne);
                break;

            case samplePushOne:
                if (!follower.isBusy() || follower.getPose().getX() <=13) {
                    follower.followPath(samplePushTwoPC);
                    autoState = AutoState.samplePushTwo;
                    follower.setCentripetalScaling(0.0008);
                }

                break;

            case samplePushTwo:
                if (follower.getPose().getX() <=13 && follower.getCurrentTValue() >= 0.5) {
                    follower.followPath(specIntakeTwoPC, true);
                    autoState = AutoState.intakingSpecTwo;
                    follower.setCentripetalScaling(0.0008);
                }
                break;

            case intakingSpecTwo:
                intakeSpec(specDepoThreePC, AutoState.depositingSpecThree , 0.7, 0.4);
                break;

            case depositingSpecThree:
                depositSpec(specIntakeThreePC, AutoState.intakingSpecThree);
                break;

            case intakingSpecThree:
                intakeSpec(specDepoFourPC, AutoState.depositingSpecFour , 0.7, 0.4);
                break;

            case depositingSpecFour:
                depositSpec(parkPC, AutoState.park);
                break;

            case park:
                if (!follower.isBusy()) {
                    autoState = AutoState.done;
                }
        }

    }

    private void buildPaths() {
        specDepoOnePC = follower
                .pathBuilder().addPath(Auto_4_0_Paths.specDepoOne)
                .setConstantHeadingInterpolation(Auto_4_0_Paths.specDepoOnePose.getHeading())
                .setPathEndTimeoutConstraint(250.0)
                .build();

        specIntakeOnePC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.specIntakeOne)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.specIntakeOnePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoTwoPC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.specDepoTwo)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.specDepoTwoPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        samplePushOnePC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.samplePushOne)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.pushPoseOne.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .setZeroPowerAccelerationMultiplier(10)
                        .build();

        samplePushTwoPC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.samplePushTwo)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.pushPoseTwo.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .setZeroPowerAccelerationMultiplier(10)
                        .build();

        specIntakeTwoPC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.specIntakeTwo)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.specIntakeTwoPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoThreePC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.specDepoThree)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.specDepoThreePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specIntakeTwoPC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.specIntakeThree)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.specIntakeThreePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoFourPC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.specDepoFour)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.specDepoFourPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        parkPC =
                follower
                        .pathBuilder().addPath(Auto_4_0_Paths.park)
                        .setConstantHeadingInterpolation(Auto_4_0_Paths.parkPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();
    }

    private void intakeSpec(PathChain nextPath, AutoState nextAutoState, double slowTValue, double slowPower) {

        // While this function is running, it expects to start with maxPower at 1.0, and specIntaking status at aligning, and will end on these same conditions

        // If we are close to the end of the path, slow down to grab spec, only if in aligning state
        if (follower.getCurrentTValue() >= slowTValue && specIntakingStatus == SpecIntakingStatus.aligning) {
            follower.setMaxPower(slowPower);
        }

        // TODO: Tune the ω and vx thresholds for intaking specs
        // Deposit must always be at spec intake position, and robot velocity (both, vx and ω) must be below the threshold for intaking specs, and specIntakingStatus must be aligning
        if (deposit.currentState == Deposit.State.specIntake && Math.abs(follower.getVelocity().getXComponent()) <= Auto_4_0_Paths.intakeVxThreshold && Math.abs(follower.getVelocity().getTheta()) <= Auto_4_0_Paths.intakeVthetaThreshold && specIntakingStatus == SpecIntakingStatus.aligning) {
            follower.holdPoint(follower.getPose());
            deposit.setClaw(Claw.State.Closed);
            specIntakingStatus = SpecIntakingStatus.intaking;
            follower.setMaxPower(1.0);
        }

        // If we are in the intaking phase, and the claw is closed, continue to next Path
        if (deposit.claw.currentState == Claw.State.Closed && specIntakingStatus == SpecIntakingStatus.intaking) {
            deposit.setTargetState(Deposit.State.specDeposit);
            follower.followPath(nextPath);
            autoState = nextAutoState;
            specIntakingStatus = SpecIntakingStatus.aligning;
        }

    }

    private void depositSpec(PathChain nextPath, AutoState nextAutoState) {

        // If Vx meets velocity constraint and the path did not just start (t>=0.1) and specDepoStatus is driving, move to releasing status
        if (Math.abs(follower.getVelocity().getXComponent()) == 0.05 && follower.getCurrentTValue() >= 0.1 && specDepoStatus == SpecDepoStatus.driving) {
            deposit.setClaw(Claw.State.Open);
            specDepoStatus = SpecDepoStatus.releasing;
            follower.holdPoint(follower.getPose());
        }

        // If the claw is open and specDepoStatus is releasing, move to next path.
        if (deposit.claw.currentState == Claw.State.Open && specDepoStatus == SpecDepoStatus.releasing) {
            deposit.setTargetState(Deposit.State.specIntake);
            follower.followPath(nextPath);
            autoState = nextAutoState;
            specDepoStatus = SpecDepoStatus.driving;
        }

    }

}