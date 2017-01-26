/**
 * Example demonstrating the velocity closed-loop servo.
 * Tested with Logitech F350 USB Gamepad inserted into Driver Station]
 * 
 * Be sure to select the correct feedback sensor using SetFeedbackDevice() below.
 *
 * After deploying/debugging this to your RIO, first use the left Y-stick 
 * to throttle the Talon manually.  This will confirm your hardware setup.
 * Be sure to confirm that when the Talon is driving forward (green) the 
 * position sensor is moving in a positive direction.  If this is not the cause
 * flip the boolena input to the SetSensorDirection() call below.
 *
 * Once you've ensured your feedback device is in-phase with the motor,
 * use the button shortcuts to servo to target velocity.  
 *
 * Tweak the PID gains accordingly.
 */
package org.usfirst.frc.team469.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Joystick.AxisType;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

public class Robot extends IterativeRobot {
  
	CANTalon _talon = new CANTalon(0);	
	Joystick _joy = new Joystick(0);	
	StringBuilder _sb = new StringBuilder();
	int _loops = 0;
	int x = 0;
	int y = 0;
	int v = 0;
	int q = 0;
	 long starttime = 0;
	 long endtime = 0;
	 long time = 0;
	 long starttime2 = 0;
	 long endtime2 = 0;
	 long time2 = 0;
	
	public void robotInit() {
		q = 0;
		v=0;
		  x = 0;
		  y = 0;
		starttime = 0;
		 endtime = 0;
		 time = 0;
        /* first choose the sensor */
        _talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        _talon.reverseSensor(false);
        _talon.configEncoderCodesPerRev(20); // if using FeedbackDevice.QuadEncoder
        //_talon.configPotentiometerTurns(XXX), // if using FeedbackDevice.AnalogEncoder or AnalogPot

        /* set the peak and nominal outputs, 12V means full */
        _talon.configNominalOutputVoltage(+0.0f, -0.0f);
        _talon.configPeakOutputVoltage(+12.0f, 0.0f);
        /* set closed loop gains in slot0 */
        _talon.setProfile(0);
        _talon.setF(1.40283);
        _talon.setP(2.8521);
        _talon.setI(0); 
        _talon.setD(28);
	}
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	/* get gamepad axis */
    	double leftYstick = _joy.getAxis(AxisType.kY);
    	double motorOutput = _talon.getOutputVoltage() / _talon.getBusVoltage();
    	/* prepare line to print */
		_sb.append("\tout:");
		_sb.append(motorOutput);
        _sb.append("\tspd:");
        _sb.append(_talon.getSpeed() );
//        System.currentTimeMillis()
        if(_joy.getRawButton(1)&&y==0){
        	starttime= System.currentTimeMillis();
        	y++;
        }
        if(_joy.getRawButton(1)){
        	/* Speed mode */
        
        	 
        	double targetSpeed = 3000; /* 1500 RPM in either direction */
        	_talon.changeControlMode(TalonControlMode.Speed);
        	_talon.set(targetSpeed); /* 1500 RPM in either direction */

        	/* append more signals to print when in speed mode. */
            _sb.append("\terr:");
            _sb.append(_talon.getClosedLoopError());
            _sb.append("\ttrg:");
            _sb.append(targetSpeed);
            if(_talon.getSpeed()>=3000 && x==0){
            	endtime = System.currentTimeMillis();
            	time = endtime-starttime;
            	System.out.print(time);
            	x++;
            }
        } else {
        	/* Percent voltage mode */
        	_talon.changeControlMode(TalonControlMode.PercentVbus);
        	_talon.set(leftYstick);
        }
        if(_joy.getRawButton(2)&&v==0){
        	starttime2= System.currentTimeMillis();
        	v++;
        }
        if(_joy.getRawButton(2)){
        	if(_talon.getSpeed()>=3000&&q==0){
        		endtime2= System.currentTimeMillis();
        		time2 = endtime2-starttime2;
        		System.out.print(time2);
        		q++;
        	}
        	if(_talon.getSpeed()<3000){
        		_talon.set(1);
        	}
        	
        }
        if(_talon.getSpeed()<2000){
    		q=0;
    		v=0;
    		x=0;
    		y=0;
    	}

        if(++_loops >= 10) {
        	_loops = 0;
        	System.out.println(_sb.toString());
        }
        _sb.setLength(0);
    }
}
