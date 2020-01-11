package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Talon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

import java.nio.Buffer;
import java.rmi.server.Operation;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import edu.wpi.first.wpilibj.interfaces.Potentiometer;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.AnalogPotentiometer;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.hal.PDPJNI;
import frc.robot.autostep.*;
import edu.wpi.first.wpilibj.Compressor;

public class Robot extends TimedRobot {

	// Sensors
	public AHRS navx = new AHRS(SPI.Port.kMXP);

	// Drivetrain
	public DriveTrain driveTrain = new DriveTrain(0, 1, 2, 3, navx);

	// Nuemataics
	public DoubleSolenoid diskBrake = new DoubleSolenoid(3, 2);

	// Joysticks
	public Joystick driver;
	public Joystick operator;
	public boolean arcadeDrive = false;
	public Joystick flightStickLeft;
	public Joystick flightStickRight;

	
	public enum DriveScale {
		linear, parabala, tangent, inverse, cb, cbrt,
	}
	enum AutoChoice {
		// Left, Right
	}

	enum RobotState {
		Autonomous, Teleop;
	}

	public RobotState currentState;

	public enum LimelightPlacement {
	};

	// Auto
	public AutoStep[] autonomous;
	public int currentAutoStep;

	public int accum;

	public void robotInit() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(2);
	}

	public void disabledInit() {
		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
	}

	public void autonomousInit() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(1);

		driveTrain.SetBreak();
		currentState = RobotState.Autonomous;

		// currentAutoStep = 0;
		// autonomous = new AutoStep[21];
	}

	public void autonomousPeriodic() {
	}

	public void DiskBrakeEnable() {
		diskBrake.set(DoubleSolenoid.Value.kReverse);
	}

	public void DiskBrakeDisable() {
		diskBrake.set(DoubleSolenoid.Value.kForward);
	}

	public void teleopInit() {
		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(2);

		currentState = RobotState.Teleop;

		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
		flightStickLeft = new Joystick(3);
		flightStickRight = new Joystick(2);
	}

	//Drive Scale
	public float DriveScaleSelector(float ControllerInput, DriveScale selection) {
		float multiplier = (ControllerInput / (float) Math.abs(ControllerInput));

		if (selection == DriveScale.parabala) {
			return multiplier * (float) Math.pow(ControllerInput, 2);

		} else if (selection == DriveScale.tangent) {

			return multiplier * (0.4f * (float) Math.tan(1.8 * (multiplier * ControllerInput) - .9) + 0.5f);

		} else if (selection == DriveScale.inverse) {

			return (float) Math.pow(ControllerInput, 1/2);

		} else if (selection == DriveScale.cb) {

			return (float) Math.pow(ControllerInput, 3);

		} else if (selection == DriveScale.cbrt) {

			return multiplier * (0.63f * (float) Math.cbrt((multiplier * ControllerInput) - 0.5f) + 0.5f);

		} else {

			return ControllerInput;

		}
	}


	public void teleopPeriodic() {

		// Turning
		// Buttons: 2=X 3=B

		/*
		 * System.out.println(navx.getYaw()); if (operator.getRawButton(8)) {
		 * navx.reset(); }
		 * 
		 * if (operator.getRawButton(3)) { driveTrain.SetRightSpeed(-0.4f);
		 * driveTrain.SetLeftSpeed(0.0f);
		 * 
		 * } else if (operator.getRawButton(2)) {
		 * 
		 * driveTrain.SetRightSpeed(0.4f); driveTrain.SetLeftSpeed(-0.0f);
		 * 
		 * } else {
		 * 
		 * driveTrain.SetRightSpeed(0f); driveTrain.SetLeftSpeed(0f);
		 * 
		 * }
		 * 
		 */

		// Disk Break
		if (flightStickRight.getRawButton(3)) {

			DiskBrakeEnable();

		} else if (flightStickRight.getRawButton(2)) {

			DiskBrakeDisable();
		}

		SmartDashboard.putNumber("UltrasonicDown", accum);
		accum++;

		// Limelight
		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry ta = table.getEntry("ta");

		double x = tx.getDouble(0.0);
		double y = ty.getDouble(0.0);
		double area = ta.getDouble(0.0);

		NetworkTableInstance.getDefault().getTable("limelight").getEntry("stream").setNumber(0);

		if (flightStickRight.getRawButton(1)) {
			float turnBuffer = 3;
			if (x > turnBuffer) {
				driveTrain.SetLeftSpeed(0.4f);
			}
		} else {
			ControllerDrive();

		}
		UpdateMotors();
	}

	public void testInit() {

		navx.reset();

		// Controllers
		driver = new Joystick(0);
		operator = new Joystick(1);
		flightStickLeft = new Joystick(3);
		flightStickRight = new Joystick(2);

	}

	public void testPeriodic() {

		ControllerDrive();
		UpdateMotors();

	}

	public void UpdateMotors() {
		driveTrain.Update();
	}

	public float TranslateController(float input) {
		float deadzone = 0.15f;
		if (input > -deadzone && input < deadzone) {
			input = 0.0f;
		}
		float a = 0.7f;
		float output = (a * input * input * input) + (1 - a) * input;
		return output;
	}

	public void ControllerDrive() {
		if (arcadeDrive) {
			// Arcade
			float horJoystick = TranslateController((float) driver.getRawAxis(4));
			float verJoystick = TranslateController((float) driver.getRawAxis(1));

			driveTrain.SetRightSpeed(-verJoystick + -horJoystick);
			driveTrain.SetLeftSpeed(-verJoystick + horJoystick);
			driveTrain.SetCoast();
		} else {
			// tank
			float leftJoystick = (float) flightStickLeft.getRawAxis(1);
			float rightJoystick = (float) flightStickRight.getRawAxis(1);

			driveTrain.SetRightSpeed(-rightJoystick);
			driveTrain.SetLeftSpeed(-leftJoystick);
			driveTrain.SetCoast();
		}
	}

	public boolean Limelight(LimelightPlacement placement) {

		NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
		NetworkTableEntry tx = table.getEntry("tx");
		NetworkTableEntry ty = table.getEntry("ty");
		NetworkTableEntry ta = table.getEntry("ta");
		NetworkTableEntry tv = table.getEntry("tv");

		double x = tx.getDouble(0.0);
		double y = ty.getDouble(0.0);
		double area = ta.getDouble(0.0);
		double value = tv.getDouble(0.0);

		driveTrain.SetBreak();

		return true;
	}
}