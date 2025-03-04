//TODO: Refactor

package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Path;
import com.pedropathing.pathgen.PathChain;
import com.pedropathing.pathgen.Point;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.AutoPaths.Auto_2_0_Paths;
import org.firstinspires.ftc.teamcode.Pedro.Constants.FConstants;
import org.firstinspires.ftc.teamcode.Pedro.Constants.LConstants;

@Autonomous(name = "Auto V0.1.0", group = "Competition")
public class Auto_2_0 extends OpMode {

    private Telemetry telemetryA;

    private Follower follower;

    private enum AutoState{
        start,
        specDepoOne,
        specIntakeOne,
        intakingSpecOne,
        specDepoTwo,
        park,
        done;
    }

    private AutoState autoState = AutoState.start;

    private PathChain specDepoOnePC, specIntakeOnePC, specDepoTwoPC, parkPC;

    @Override
    public void init() {
        // Deposit Setup


        // Pedro & Path Setup
        Auto_2_0_Paths.build();

        Constants.setConstants(FConstants.class, LConstants.class);
        follower = new Follower(hardwareMap);

        follower.setStartingPose(Auto_2_0_Paths.startPose);

        buildPaths();

        // Telemetry
        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryA.addLine("Auto Ready");
        telemetryA.update();
    }

    @Override
    public void loop() {

        autoStateUpdate();

        follower.update();

        telemetryA.addData("Auto State", autoState);
        telemetryA.update();
    }

    public void autoStateUpdate(){
        switch (autoState) {
            case start:
                //TODO: Add check on if claw is closed here
                if (true) {
                    follower.followPath(specDepoOnePC);
                    autoState = AutoState.specDepoOne;
                }
                break;

            case specDepoOne:
                if (!follower.isBusy()) {
                    //TODO: Add logic to release claw
                    follower.followPath(specIntakeOnePC, true);
                    autoState = AutoState.specIntakeOne;
                }
                break;

            case intakingSpecOne:
                //TODO: Add logic & check that moves deposit to intaking position
                if (!follower.isBusy()) {
                    //TODO: ^Check if the robot is at a position to intake the spec, & Add logic that closes the claw
                    if (true) {
                        //TODO: ^Check if claw is closed
                        follower.followPath(specDepoTwoPC);
                        autoState = AutoState.specDepoTwo;
                    }
                }
                break;

            case specDepoTwo:
                if (!follower.isBusy()) {
                    //TODO: Add logic to release claw here
                    follower.followPath(parkPC);
                    autoState = AutoState.park;
                }
                break;

            case park:
                if (!follower.isBusy()) {
                    autoState = AutoState.done;
                }
        }

    }

    private void buildPaths() {
        specDepoOnePC = follower
                .pathBuilder().addPath(Auto_2_0_Paths.specDepoOne)
                .setConstantHeadingInterpolation(Auto_2_0_Paths.specDepoOnePose.getHeading())
                .setPathEndTimeoutConstraint(250.0)
                .build();

        specIntakeOnePC =
                follower
                        .pathBuilder().addPath(Auto_2_0_Paths.specIntakeOne)
                        .setConstantHeadingInterpolation(Auto_2_0_Paths.specIntakeOnePose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        specDepoTwoPC =
                follower
                        .pathBuilder().addPath(Auto_2_0_Paths.specDepoTwo)
                        .setConstantHeadingInterpolation(Auto_2_0_Paths.specDepoTwoPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();

        parkPC =
                follower
                        .pathBuilder().addPath(Auto_2_0_Paths.park)
                        .setConstantHeadingInterpolation(Auto_2_0_Paths.parkPose.getHeading())
                        .setPathEndTimeoutConstraint(250.0)
                        .build();
    }

}