package org.firstinspires.ftc.teamcode.commands;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.VerticalLiftSubsystem;

public class RaiseLift extends CommandBase {
    private final VerticalLiftSubsystem liftSubsystem;
    private final int position;
    private final double speed;

    public RaiseLift(VerticalLiftSubsystem subsystem, int position, double speed) {
        liftSubsystem = subsystem;
        addRequirements(liftSubsystem);

        this.position = position;
        this.speed = speed;
    }

    @Override
    public void initialize() {
        liftSubsystem.liftToPosition(position);
        liftSubsystem.setPower(speed);
    }

    @Override
    public void execute() {
        if (liftSubsystem.atTargetPosition()) {
            isFinished();
        }
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
