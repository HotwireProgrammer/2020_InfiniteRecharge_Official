package frc.robot.autostep;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Timer;
import com.kauailabs.navx.frc.AHRS;

import frc.robot.DriveTrain;

public class IntakeDrop extends AutoStep {

public DoubleSolenoid intakeSolenoid;

    public IntakeDrop(AHRS navx) {
        super();
    }

    public void Begin() {     
		System.out.println("Enabled");
		SmartDashboard.putBoolean("intakeExtended", true);
		intakeSolenoid.set(DoubleSolenoid.Value.kReverse);
    }

    public void Update() {
        isDone = true;
    }
}