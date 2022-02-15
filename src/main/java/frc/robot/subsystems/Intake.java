// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Intake extends SubsystemBase {
  private CANSparkMax intakeMotor;
  private Solenoid intakeSol;
  private boolean bool;
  private double speed;

  ShuffleboardTab tab = Shuffleboard.getTab("Intake System");

  /** Creates a new Intake. */
  public Intake() {
    this.intakeMotor = new CANSparkMax(Constants.INTAKE_MOTOR, MotorType.kBrushless);
    this.intakeSol = new Solenoid(PneumaticsModuleType.REVPH, Constants.INTAKE_SOLENOID);
    
    resetMotors();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    shuffleInit();
  }

  public void runIntake(double speed, boolean bool) {
    this.intakeMotor.set(speed);
    this.intakeSol.set(bool);

    this.bool = bool;
    this.speed = speed;
  }

  private void resetMotors() {
    this.intakeMotor.restoreFactoryDefaults();

    this.intakeMotor.setIdleMode(IdleMode.kCoast);
  }

  private void shuffleInit() {
    tab.add("Intake Velcocity (RPM)", this.intakeMotor.getEncoder().getVelocity());
    tab.add("Intake Velcocity (%)", this.speed);
    tab.add("Intake Position", this.intakeMotor.getEncoder().getPosition());
    tab.addBoolean("Solenoid on?", () -> this.bool);
  }
}