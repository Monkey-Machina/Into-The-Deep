package org.firstinspires.ftc.teamcode.AutoPaths;

import com.pedropathing.localization.Pose;
import com.pedropathing.pathgen.BezierLine;

public class Auto_2_0_Paths {

    public static Pose startPose = new Pose(6.299, 66.114, Math.toRadians(180));

    public static Pose
        specDepoOnePose = new Pose(41.000, 74.000, Math.toRadians(180)),
        specDepoTwoPose = new Pose(41.000, 72.000, Math.toRadians(180)),

        specIntakeOnePose = new Pose(6.299,34.000, Math.toRadians(180)),

        parkPose =  new Pose(6.299,34.000, Math.toRadians(180));

    public static BezierLine
        specDepoOne,
        specIntakeOne,
        specDepoTwo,
        park;


    public  static void build() {
        specDepoOne = new BezierLine(startPose, specDepoOnePose);

        specIntakeOne = new BezierLine(specDepoOnePose, specIntakeOnePose);

        specDepoTwo = new BezierLine(specIntakeOnePose, specDepoTwoPose);

        park = new BezierLine(specDepoTwoPose, parkPose);
        }

}
