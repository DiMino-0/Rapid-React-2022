// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Shooter extends SubsystemBase {
  private CANSparkMax top, bottom;

  ShuffleboardTab tab = Shuffleboard.getTab("Shooter System");

  private double kIz, kMinOutput, kMaxOutput;

  SparkMaxPIDController top_pidcontroller, bottom_pidcontroller;

  private double topSpeed, bottomSpeed;

  private double distance; // inches, convert to whatever you need in the run command or shuffleboard
                           // command

  NetworkTableEntry setPointTop, kPTop, kITop, kDTop, kFFTop;
  NetworkTableEntry setPointBottom, kPBottom, kIBottom, kDBottom, kFFBottom;

  NetworkTableEntry speedTop, speedBottom; 

  NetworkTableEntry topVelRPM, topVelPercent, 
                    bottomVelRPM, bottomVelPercent,
                    targetValidity, limelightX, limelightY,
                     limelightArea, targetDistance, voltage; 

  private int goalHeight = 104; // inches
  private int limelightHeight = 20; // inches
  private int limelightAngle = 35; // degrees

  private NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

  private NetworkTableEntry tx = table.getEntry("tx");
  private NetworkTableEntry ty = table.getEntry("ty");
  private NetworkTableEntry ta = table.getEntry("ta");
  private NetworkTableEntry tv = table.getEntry("tv");

  /** Creates a new Shooter. */
  public Shooter() {

    this.top = new CANSparkMax(Constants.SHOOTER_TOP, MotorType.kBrushless);
    this.bottom = new CANSparkMax(Constants.SHOOTER_BOTTOM, MotorType.kBrushless);

    resetMotors();
    PIDinit();
    shuffleInit();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    updateD();
    shuffleUpdate();
    //System.out.println(this.distance);
  }

  public void toggleLimelightLight(int value) {
    table.getEntry("ledMode").setNumber(value);
  }

  public void toggleLimelightCamMode(int value) {
    table.getEntry("camMode").setNumber(value);
  }

  public void runShooter(double topSpeed, double bottomSpeed) {
    this.top.set(-topSpeed);
    this.bottom.set(bottomSpeed);

    this.topSpeed = topSpeed;
    this.bottomSpeed = bottomSpeed;
  }

  public void runPID()
  {
    runTop();
    runBottom();
  }

  public void runTop()
  {
    top_pidcontroller.setP(kPTop.getDouble(0));
    top_pidcontroller.setI(kITop.getDouble(0));
    top_pidcontroller.setD(kDTop.getDouble(0));
    top_pidcontroller.setFF(kFFTop.getDouble(0));
    top_pidcontroller.setIZone(kIz);
    top_pidcontroller.setOutputRange(kMinOutput, kMaxOutput);

    top_pidcontroller.setReference(-setPointTop.getDouble(0), ControlType.kVelocity);
    speedTop.setDouble(top.getEncoder().getVelocity());
  }

  public void runBottom()
  {
    bottom_pidcontroller.setP(kPBottom.getDouble(0));
    bottom_pidcontroller.setI(kIBottom.getDouble(0));
    bottom_pidcontroller.setD(kDBottom.getDouble(0));
    bottom_pidcontroller.setFF(kFFBottom.getDouble(0));
    bottom_pidcontroller.setIZone(kIz);
    bottom_pidcontroller.setOutputRange(kMinOutput, kMaxOutput);

    //setPointBottom.getDouble(0)
    bottom_pidcontroller.setReference(equationBottom(this.distance), ControlType.kVelocity);
    speedBottom.setDouble(top.getEncoder().getVelocity());
  }

  public void run(){
    runTop();
    runBottom();
  }

  private double equationBottom(double distanceInches){
    double distanceFeet = distanceInches / 12.0;
    return 228*distanceFeet - 1188;
  }

  private void resetMotors() {
    this.top.restoreFactoryDefaults();
    this.bottom.restoreFactoryDefaults();
    /*
     * this decreases the time between shots
     * by leaving the motors at a higher speed when not in use
     */
    this.top.setIdleMode(IdleMode.kCoast);
    this.bottom.setIdleMode(IdleMode.kCoast);
  }

  private void shuffleInit() {
    topVelRPM = tab.add("Velocity Top(RPM)", this.top.getEncoder().getVelocity()).getEntry();
    topVelPercent = tab.add("Velocity Top(%)", this.topSpeed).getEntry();

    bottomVelRPM = tab.add("Velocity Bottom(RPM)", this.bottom.getEncoder().getVelocity()).getEntry();
    bottomVelPercent = tab.add("Velocity Bottom(%)", this.bottomSpeed).getEntry();

    NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(0);
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("pipeline").setNumber(0);

    targetValidity = tab.add("Valid Target?", tv.getBoolean(false)).getEntry();
    limelightX = tab.add("Limelight X", tx.getDouble(0)).getEntry();
    limelightY = tab.add("Limelight Y", ty.getDouble(0)).getEntry();
    limelightArea = tab.add("Limelight Area", ta.getDouble(0)).getEntry();

    targetDistance = tab.add("Distance to target", this.distance).getEntry();

    voltage = tab.add("Top Shooter Volts", top.getBusVoltage()).getEntry();
  }

  private void shuffleUpdate() {
    topVelRPM.setDouble(this.top.getEncoder().getVelocity());
    topVelPercent.setDouble(this.topSpeed);

    bottomVelRPM.setDouble(this.bottom.getEncoder().getVelocity());
    bottomVelPercent.setDouble(this.bottomSpeed);

    targetValidity.setBoolean(tv.getBoolean(false));
    limelightX.setDouble(tx.getDouble(0));
    limelightY.setDouble(ty.getDouble(0));
    limelightArea.setDouble(ta.getDouble(0));

    targetDistance.setDouble(this.distance);
    voltage.setDouble(top.getBusVoltage());
  }

  private void PIDinit()
  {
    //name that tab
    this.tab = Shuffleboard.getTab("Shooter System");

    //top
    this.setPointTop = tab.add("Set Speed (Top)", 5000).getEntry();
    this.speedTop = tab.add("Actual Speed (Top)", 0).getEntry();
    this.kPTop = tab.addPersistent("P (Top)", Constants.kPTop).getEntry();
    this.kITop = tab.addPersistent("I (Top)", Constants.kITop).getEntry();
    this.kDTop = tab.addPersistent("D (Top)", Constants.kDTop).getEntry();
    this.kFFTop = tab.addPersistent("F (Top)", Constants.kFTop).getEntry();

    //bottom
    this.setPointBottom = tab.add("Set Speed (Bottom)", 5000).getEntry();
    this.speedBottom = tab.add("Actual Speed (Bottom)", 0).getEntry();
    this.kPBottom = tab.addPersistent("P (Bottom)", Constants.kPBottom).getEntry();
    this.kIBottom = tab.addPersistent("I (Bottom)", Constants.kIBottom).getEntry();
    this.kDBottom = tab.addPersistent("D (Bottom)", Constants.kDBottom).getEntry();
    this.kFFBottom = tab.addPersistent("F (Bottom)", Constants.kFBottom).getEntry();

    this.top_pidcontroller = top.getPIDController();
    this.bottom_pidcontroller = bottom.getPIDController();

    this.kIz = 100;
    this.kMinOutput = -1;
    this.kMaxOutput = 1;
  }

  private void updateD() {
    /*
     * d = (h2-h1) / tan(a1+a2)
     * d = (hieght of the limelight from ground minus the heigth of the field goal
     * inches) over
     * (tan(angle of lens to goal + angle of lens from bottom of camera))
     */
    // Collect data and run linear regression for motor power to distance linear
    // relationship to implement to shoot command
    this.distance = (this.goalHeight - this.limelightHeight)
        / Math.tan(Math.toRadians(this.limelightAngle + ty.getDouble(0)));
  }
}