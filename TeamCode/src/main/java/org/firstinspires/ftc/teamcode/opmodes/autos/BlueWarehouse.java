package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.robot.Lift;
import org.firstinspires.ftc.teamcode.robot.Webcam1;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous
public class BlueWarehouse extends LinearOpMode {
    private Webcam1 webcam;

    private Lift lift;

    private SampleMecanumDrive bot;

    private int elementPosition = 2;

    @Override
    public void runOpMode() throws InterruptedException {
        webcam = new Webcam1(hardwareMap);
        webcam.startTeamelementColor();

        lift = new Lift(hardwareMap, telemetry);

        // Energize the tape servos so they don't move
        Servo tapeYaw = hardwareMap.get(Servo.class, "tapeYaw");
        tapeYaw.setPosition(0.25);
        Servo tapePitch = hardwareMap.get(Servo.class, "tapePitch");
        tapePitch.setPosition(0.7);

        bot = new SampleMecanumDrive(hardwareMap);

        Pose2d start = new Pose2d(13, 62, Math.toRadians(270));
        bot.setPoseEstimate(start);

        TrajectorySequence initialToHub = bot.trajectorySequenceBuilder(start)
                .splineTo(new Vector2d(2, 36), Math.toRadians(220))
                .build();

        TrajectorySequence toWarehouse = bot.trajectorySequenceBuilder(initialToHub.end())
                .setReversed(true)
                .lineToLinearHeading(new Pose2d(10, 67, 0))
                .build();

        TrajectorySequence throughGap = bot.trajectorySequenceBuilder(toWarehouse.end())
                .lineTo(new Vector2d(45, 67))
                .waitSeconds(1.5)
                .build();

        TrajectorySequence returnToHub = bot.trajectorySequenceBuilder(throughGap.end())
                .setReversed(true)
                .lineTo(new Vector2d(0, 67))
                .lineTo(new Vector2d(-12, 65))
                .setReversed(false)
                .turn(-Math.toRadians(90))
                .forward(17)
                .build();

        TrajectorySequence finalToWarehouse = bot.trajectorySequenceBuilder(returnToHub.end())
                .setReversed(true)
                .lineToLinearHeading(new Pose2d(8, 68, Math.toRadians(15)))
                .lineTo(new Vector2d(45, 68))
                .strafeRight(25)
                .build();

        telemetry.addLine("Ready for Start");
        telemetry.update();

        waitForStart();

        elementPosition = webcam.getElementPosition();
        webcam.stop();

        telemetry.addData("Going to level:", elementPosition);
        telemetry.update();

        bot.followTrajectorySequence(initialToHub);
        navigateToLevel();

        bot.followTrajectorySequence(toWarehouse);

        // re-localize?

        lift.toGroundPickUp();

        bot.followTrajectorySequence(throughGap);

        lift.resetPickUp();

        bot.followTrajectorySequence(returnToHub);
        lift.level2(false);

        bot.followTrajectorySequence(finalToWarehouse);
    }

    private void navigateToLevel() {
        switch (elementPosition) {
            case 0:
                lift.level0(true);
                break;

            case 1:
                lift.level1(true);
                break;

            case 2:
                lift.level2(true);
                break;
        }
    }
}
