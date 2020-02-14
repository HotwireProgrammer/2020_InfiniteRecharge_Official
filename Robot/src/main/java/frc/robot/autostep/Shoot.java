package frc.robot.autostep;

import frc.robot.Shooter;

//import javax.swing.Timer;
import edu.wpi.first.wpilibj.Timer;

import frc.robot.Indexer;

public class Shoot extends AutoStep {

    public Shooter shooter;
    public double rpmTarget;
    public Indexer indexer;
    public int ballsShot = 0;
    public int ballsShotTarget;
    public boolean beamBreakClear = false;
    public Timer fifthBallTimer;

    public Shoot(Shooter shooter, Indexer indexer, double rpmTarget, int ballsShotTarget) {
        super();
        this.shooter = shooter;
        this.rpmTarget = rpmTarget;
        this.indexer = indexer;
        this.ballsShotTarget = ballsShotTarget;

        autoIndex = false;
    }

    public void Begin() {
        shooter.rpmTarget = rpmTarget;
        fifthBallTimer = new Timer();
        fifthBallTimer.reset();
    }

    public void Update() {
        System.out.println(ballsShot);
        if (indexer.beamBreakTop.get()) {
            beamBreakClear = true;
        }
        if (!indexer.beamBreakTop.get() && beamBreakClear && shooter.UpToSpeed()) {
            beamBreakClear = false;
            ballsShot++;
        }
        if (ballsShot == 5) {
            fifthBallTimer.start();
            ballsShot++;
        }
        if (fifthBallTimer.get() > 0.1) {
            isDone = true;
            fifthBallTimer.stop();
        }


        indexer.RunManualForward();

    }
}