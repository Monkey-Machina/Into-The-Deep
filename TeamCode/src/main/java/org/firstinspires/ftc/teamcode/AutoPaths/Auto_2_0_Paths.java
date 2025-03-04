package org.firstinspires.ftc.teamcode.AutoPaths;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierCurve;
import com.pedropathing.pathgen.BezierLine;
import com.pedropathing.pathgen.Point;

public class Auto_2_0_Paths {

    public static Pose startPose = new Pose(6.299, 66.114, Math.toRadians(180));

    public static Pose
        specDepoOnePose = new Pose(42.000, 74.000, Math.toRadians(180)),
        specDepoTwoPose = new Pose(42.000, 72.000, Math.toRadians(180)),

        specIntakeOnePose = new Pose(6.5,34.000, Math.toRadians(180)),

        parkPose =  new Pose(6.299,34.000, Math.toRadians(180));

    public static BezierLine
        specDepoOne,
        specDepoTwo,
        park;

    public static BezierCurve
        specIntakeOne;

    public  static void build() {
        specDepoOne = new BezierLine(startPose, specDepoOnePose);

        specIntakeOne = new BezierCurve(
                specDepoOnePose,
                new Pose(22.000, 67.000, Point.CARTESIAN),
                new Pose(28.000, 34.000, Point.CARTESIAN),
                new Pose(20.000, 34.000, Point.CARTESIAN),
                specIntakeOnePose
        );

        specDepoTwo = new BezierLine(specIntakeOnePose, specDepoTwoPose);

        park = new BezierLine(specDepoTwoPose, parkPose);
        }

}
