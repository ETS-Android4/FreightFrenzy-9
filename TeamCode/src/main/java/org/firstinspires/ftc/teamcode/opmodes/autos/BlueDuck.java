package org.firstinspires.ftc.teamcode.opmodes.autos;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.ParallelDeadlineGroup;
import com.arcrobotics.ftclib.command.ParallelRaceGroup;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.commandgroup.PickLevel;
import org.firstinspires.ftc.teamcode.commandgroup.ReturnLift;
import org.firstinspires.ftc.teamcode.commands.FollowTrajectoryCommand;
import org.firstinspires.ftc.teamcode.commands.IntakeCube;
import org.firstinspires.ftc.teamcode.commands.OuttakeCube;
import org.firstinspires.ftc.teamcode.commandgroup.Reset;
import org.firstinspires.ftc.teamcode.commands.SpinCarousel;
import org.firstinspires.ftc.teamcode.roadrunner.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.teamcode.util.Webcam1;
import org.firstinspires.ftc.teamcode.subsystems.DuckWheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.HorizontalLiftSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VerticalLiftSubsystem;


@Autonomous(group = "Blue")
public class BlueDuck extends CommandOpMode {
    private Webcam1 webcam;
    private int elementPosition = 2;

    private SampleMecanumDrive drive;
    private DuckWheelSubsystem duck;
    private HorizontalLiftSubsystem horizontalLift;
    private VerticalLiftSubsystem verticalLift;
    private IntakeSubsystem intake;

    @Override
    public void initialize() {
        telemetry.addLine("Creating Subsystems");
        telemetry.update();

        webcam = new Webcam1(hardwareMap);

        // Energize the tape servos so they don't move
        Servo tapeYaw = hardwareMap.get(Servo.class, "tapeYaw");
        Servo tapePitch = hardwareMap.get(Servo.class, "tapePitch");

        duck = new DuckWheelSubsystem(hardwareMap);
        horizontalLift = new HorizontalLiftSubsystem(hardwareMap, telemetry);
        verticalLift = new VerticalLiftSubsystem(hardwareMap, telemetry);
        intake = new IntakeSubsystem(hardwareMap);

        drive = new SampleMecanumDrive(hardwareMap);

        telemetry.addLine("Creating Paths");
        telemetry.update();

        Pose2d start = new Pose2d(-36.0, 62, Math.toRadians(270));
        drive.setPoseEstimate(start);

        TrajectorySequence toDuck = drive.trajectorySequenceBuilder(start)
                .lineTo(new Vector2d(-50, 50))
                .lineTo(new Vector2d(-62, 55))
                .waitSeconds(1)
                .build();

        TrajectorySequence toHub = drive.trajectorySequenceBuilder(toDuck.end())
                .lineTo(new Vector2d(-63, 40))
                .lineTo(new Vector2d(-55, 24))
                .turn(Math.toRadians(90))
                .build();

        TrajectorySequence approachHub = drive.trajectorySequenceBuilder(toHub.end())
                .lineTo(new Vector2d(-32, 24))
                .build();

        TrajectorySequence toSquare = drive.trajectorySequenceBuilder(toHub.end())
                .lineTo(new Vector2d(-59, 39))
                .build();

        telemetry.addLine("Starting Webcam");
        telemetry.update();

        webcam.startTeamelementColor();

        telemetry.addLine("Ready to Start");
        telemetry.update();

        waitForStart();

        tapeYaw.setPosition(0.25);
        tapePitch.setPosition(0.5);

        elementPosition = webcam.getElementPosition();
        telemetry.addData("Going to position", elementPosition);

        telemetry.addLine("Scheduling Tasks");
        telemetry.update();

        schedule(
            new SequentialCommandGroup(
                new InstantCommand(() -> webcam.stop()),
                new Reset(verticalLift, horizontalLift),
                new ParallelRaceGroup(
                  new WaitCommand(250),
                  new IntakeCube(intake)
                ),
                new FollowTrajectoryCommand(drive, toDuck),
                new ParallelDeadlineGroup(
                    new WaitCommand(3000),
                    new SpinCarousel(duck, true)
                ),
                new FollowTrajectoryCommand(drive, toHub),
                new ParallelCommandGroup(
                    new FollowTrajectoryCommand(drive, approachHub),
                    new PickLevel(elementPosition, verticalLift, horizontalLift, intake)
                ),
                new ParallelRaceGroup(
                        new WaitCommand(2000),
                        new OuttakeCube(intake)
                ),
                new ParallelCommandGroup(
                    new FollowTrajectoryCommand(drive, toSquare),
                    new ReturnLift(verticalLift, horizontalLift)
                )
            )
        );

        register(verticalLift, horizontalLift, intake, duck);

    }

}
