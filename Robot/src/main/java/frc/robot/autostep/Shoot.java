package frc.robot.autostep;

import frc.robot.Shooter;
import frc.robot.Indexer;

public class Shoot extends AutoStep {

    public Shooter shooter;
    public double rpmTarget;
    public Indexer indexer;

    public Shoot(Shooter shooter, Indexer indexer, double rpmTarget) {
        super();
        this.shooter = shooter;
        this.rpmTarget = rpmTarget;
        this.indexer = indexer;
    }

    public void Begin() {
        shooter.rpmTarget = rpmTarget;
    }

    public void Update() {
        if (shooter.rpm > rpmTarget - (rpmTarget * 0.05)) {
            indexer.RunManualForward();
        }
    }
}