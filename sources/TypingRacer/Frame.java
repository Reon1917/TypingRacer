package TypingRacer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

@SuppressWarnings("serial")
public class Frame extends JFrame implements ActionListener, KeyListener
{
	String passage=""; //Passage we get
	String typedPass=""; //Passage the user types
	String message=""; //Message to display at the end of the TypingTest
	 
	int typed=0; //typed stores till which character the user has typed
	int count=0;
	int WPM;
	int timeRemaining;
	int testDuration;
	
	double start; 
	double end; 
	double elapsed;
	double seconds;
	
	boolean running; //If the person is typing
	boolean ended; //Whether the typing test has ended or not
	
	final int SCREEN_WIDTH;
	final int SCREEN_HEIGHT;
	final int DELAY=100; 
	long startTime;
	
	JButton button; 
	Timer timer;
	JLabel label; 
	JLabel instructionsLabel;

	
	public Frame()
	{
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		SCREEN_WIDTH=800;
		SCREEN_HEIGHT=600;
		this.setSize(SCREEN_WIDTH,SCREEN_HEIGHT);
		this.setVisible(true); 
		this.setLocationRelativeTo(null); 
		ImageIcon icon = new ImageIcon("keyboard.png");
		this.setIconImage(icon.getImage());
		
		instructionsLabel = new JLabel("<html><div style='text-align: center;'>" +
                "<b>How to play?</b><br>" +
                "Step 1: Click the start button.<br>" +
                "Step 2: Input the text you wish to type in the text box.<br>" +
                "Step 3: Input the time limit.</div></html>");
        instructionsLabel.setFont(new Font("Inter", Font.BOLD, 18));
        instructionsLabel.setBackground(Color.white);
        instructionsLabel.setForeground(Color.black);
        instructionsLabel.setVisible(true);
        instructionsLabel.setOpaque(true);
        this.add(instructionsLabel, BorderLayout.PAGE_START);
		
		
		button=new JButton("Start");
		button.setFont(new Font("Inter",Font.BOLD,20));
		button.setForeground(Color.white);
		button.setBackground(Color.blue);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setFocusable(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setVisible(true);
		button.addActionListener(this);
		
		
		
		
		label=new JLabel();
		label.setText("Typing Racer \n Modified Version");
		label.setFont(new Font("Inter",Font.BOLD,25));
		label.setVisible(true);
		label.setOpaque(true);
		label.setHorizontalAlignment(JLabel.CENTER); 
		label.setBackground(Color.cyan);
		
		
		

		
		
		this.add(button, BorderLayout.SOUTH);
		this.add(label, BorderLayout.CENTER);
		this.getContentPane().setBackground(Color.LIGHT_GRAY);
		this.addKeyListener(this);
		this.setFocusable(true); 
		this.setResizable(false);
		this.setTitle("Typing Racer");
		this.revalidate(); 
	}

	@Override
	public void paint(Graphics g)	
	{
		super.paint(g);
		draw(g); 
	}
	
	public void draw(Graphics g)
	{
		g.setFont(new Font("Inter", Font.BOLD, 25));
	  
	    int lineHeight = g.getFont().getSize()+5;
	    

	    if (running) {
	        int yPos = lineHeight * 5;
	        int maxLineLength = 50; // Maximum characters per line
	        

	        // Display the passage text in black
	        g.setColor(Color.BLACK);

	        // Split the passage into words
	        String[] words = passage.split(" ");
	        StringBuilder currentLine = new StringBuilder();

	        for (String word : words) {
	            // Check if adding the word exceeds the maximum line length
	            if (currentLine.length() + word.length() + 1 > maxLineLength) {
	                g.drawString(currentLine.toString(), g.getFont().getSize(), yPos);
	                yPos += lineHeight;
	                currentLine = new StringBuilder();
	                currentLine.append(word).append(" ");
	            } else {
	                currentLine.append(word).append(" ");
	            }
	        }

	        // Draw the last line
	        g.drawString(currentLine.toString(), g.getFont().getSize(), yPos);

	        // Display correctly typed passage in GREEN
	        g.setColor(Color.GREEN);
	        yPos = lineHeight * 5; // Reset yPos to the top of the passage
	        

	        // Split the typedPass into words
	        String[] typedWords = typedPass.split(" ");
	        currentLine = new StringBuilder();

	        for (String word : typedWords) {
	            // Check if adding the word exceeds the maximum line length
	            if (currentLine.length() + word.length() + 1 > maxLineLength) {
	                g.drawString(currentLine.toString(), g.getFont().getSize(), yPos);
	                yPos += lineHeight;
	                currentLine = new StringBuilder();
	                currentLine.append(word).append(" ");
	            } else {
	                currentLine.append(word).append(" ");
	            }
	        }

	        // Draw the last typed line
	        g.drawString(currentLine.toString(), g.getFont().getSize(), yPos);

	        running = false; // Set running to false once we have drawn the typed text
	    }

	

	    if (ended) {
	        if (WPM <= 40) {
	            message = "You are an Average Typist";
	        } else if (WPM > 40 && WPM <= 60) {
	            message = "You are a Good Typist";
	        } else if (WPM > 60 && WPM <= 100) {
	            message = "You are an Excellent Typist";
	        } else {
	            message = "You are an Elite Typist";
	        }

	        FontMetrics metrics = getFontMetrics(g.getFont());
	        g.setColor(Color.BLUE);
	        g.drawString("Typing Test Completed!", (SCREEN_WIDTH - metrics.stringWidth("Typing Test Completed!")) / 2, g.getFont().getSize() * 6);

	        g.setColor(Color.BLACK);
	        g.drawString("Typing Speed: " + WPM + " Words Per Minute", (SCREEN_WIDTH - metrics.stringWidth("Typing Speed: " + WPM + " Words Per Minute")) / 2, g.getFont().getSize() * 9);
	        g.drawString(message, (SCREEN_WIDTH - metrics.stringWidth(message)) / 2, g.getFont().getSize() * 11);

	        timer.stop();
	        ended = false;
	    }
	
	
	        running = false;
	    

		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	    if (passage.length() > 1) {
	        if (count == 0) {
	            start = LocalTime.now().toNanoOfDay();
	        } else if (count == passage.length()) { // Check if the user has completed typing the entire passage
	            end = LocalTime.now().toNanoOfDay();
	            elapsed = end - start;
	            seconds = elapsed / 1000000000.0; // nano/1000000000.0 is seconds
	            WPM = (int) (((passage.length() / 5) / seconds) * 60); // Calculate WPM for the entire passage
	            ended = true;
	            running = false;
	            count++;
	            repaint(); // Repaint to show the end game screen
	            return;
	        }

	        char[] pass = passage.toCharArray();
	        if (typed < passage.length()) {
	            running = true;
	            if (e.getKeyChar() == pass[typed]) {
	                typedPass = typedPass + pass[typed];
	                typed++;
	                count++;
	            }
	        }
	    }
	}


	@Override
	public void keyPressed(KeyEvent e) 
	{
		
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		
	}

	 @Override
	    public void actionPerformed(ActionEvent e) {
		 if (e.getSource() == button) {
			 this.remove(label);
			 instructionsLabel.setVisible(false);
			 this.revalidate();
			
		
			
	            // Ask the user to input a passage
	            String customPassage = JOptionPane.showInputDialog(this, "Enter a passage for typing:");
	            if (customPassage != null && !customPassage.isEmpty()) {
	                // If the user entered a passage, ask for the test duration
	                String durationString = JOptionPane.showInputDialog(this, "Enter the test duration (in seconds):");
	                try {
	                		testDuration = Integer.parseInt(durationString);
	                    if (testDuration <= 0) {
	                        JOptionPane.showMessageDialog(this, "Please enter a valid test duration (greater than 0).");
	                    } else {
	                        // Start the typing test with the custom passage and duration
	                    	timeRemaining = testDuration;
	                        passage = customPassage;
	                        timer = new Timer(100, this);
	                        timer.start();
	                        running = true;
	                        ended = false;
	                        typedPass = "";
	                        message = "";
	                        typed = 0;
	                        count = 0;
	                        startTime = System.currentTimeMillis();
	                    }
	                } catch (NumberFormatException ex) {
	                    JOptionPane.showMessageDialog(this, "Please enter a valid number for the test duration.");
	                }
	            }
	        }
		 if (running) {
	            long currentTime = System.currentTimeMillis();
	            long elapsedTime = currentTime - startTime;
	            timeRemaining = (int) ((testDuration * 1000 - elapsedTime) / 1000); // Calculate remaining time in seconds

	            if (timeRemaining <= 0) {
	            	
	                timer.stop();
	                typedPass = ""; // Clear the typed passage
	                passage = "Game Over Time Ran Out";	                
	                
	                repaint();
	                
	            }

	            repaint();
	        }
	            
	        if (ended)
	            repaint();
	    }
	public static String getPassage()
	{
		ArrayList<String> Passages=new ArrayList<String>();
		String pas1="Many touch typists also use keyboard shortcuts or hotkeys when typing on a computer. This allows them to edit their document without having to take their hands off the keyboard to use a mouse. An example of a keyboard shortcut is pressing the Ctrl key plus the S key to save a";
		String pas2="A virtual assistant (typically abbreviated to VA) is generally self-employed and provides professional administrative, technical, or creative assistance to clients remotely from a home office. Many touch typists also use keyboard shortcuts or hotkeys when typing on a computer";
		String pas3="Frank Edward McGurrin, a court stenographer from Salt Lake City, Utah who taught typing classes, reportedly invented touch typing in 1888. On a standard keyboard for English speakers the home row keys are: \"ASDF\" for the left hand and \"JKL;\" for the right hand. The keyboar";
		String pas4="Income before securities transactions was up 10.8 percent from $13.49 million in 1982 to $14.95 million in 1983. Earnings per share (adjusted for a 10.5 percent stock dividend distributed on August 26) advanced 10 percent to $2.39 in 1983 from $2.17 in 1982. Earnings may rise ";
		String pas5="Historically, the fundamental role of pharmacists as a healthcare practitioner was to check and distribute drugs to doctors for medication that had been prescribed to patients. In more modern times, pharmacists advise patients and health care providers on the selection, dosage";
		String pas6="Proofreader applicants are tested primarily on their spelling, speed, and skill in finding errors in the sample text. Toward that end, they may be given a list of ten or twenty classically difficult words and a proofreading test, both tightly timed. The proofreading test will o";
		String pas7="In one study of average computer users, the average rate for transcription was 33 words per minute, and 19 words per minute for composition. In the same study, when the group was divided into \"fast\", \"moderate\" and \"slow\" groups, the average speeds were 40 wpm, 35 wpm, an";
		String pas8="A teacher's professional duties may extend beyond formal teaching. Outside of the classroom teachers may accompany students on field trips, supervise study halls, help with the organization of school functions, and serve as supervisors for extracurricular activities. In some e";
		String pas9="Web designers are expected to have an awareness of usability and if their role involves creating mark up then they are also expected to be up to date with web accessibility guidelines. The different areas of web design include web graphic design; interface design; authoring, i";
		String pas10="A data entry clerk is a member of staff employed to enter or update data into a computer system. Data is often entered into a computer from paper documents using a keyboard. The keyboards used can often have special keys and multiple colors to help in the task and speed up th";
		
		Passages.add(pas1);
		Passages.add(pas2);
		Passages.add(pas3);
		Passages.add(pas4);
		Passages.add(pas5);
		Passages.add(pas6);
		Passages.add(pas7);
		Passages.add(pas8);
		Passages.add(pas9); 
		Passages.add(pas10);
		
		Random rand=new Random();
		int place=(rand.nextInt(10));
		
		String toReturn=Passages.get(place).substring(0,200); 
		if(toReturn.charAt(199)==32) 
		{
			toReturn=toReturn.strip(); 
			toReturn=toReturn+"."; //Adding a full stop at the last instead of a space
		}
		return(toReturn); 
	}
}