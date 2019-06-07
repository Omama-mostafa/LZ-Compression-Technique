package LZ;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

class LZData
{
	private int number;
	private char symbol;
	
	LZData()
	{
		number = 0;
		symbol = ' ';
	}
	
	public int get_number()
	{
		return number;
	}
	
	void set_number(int num)
	{
		number = num;
	}
	
	public char get_symbol()
	{
		return symbol;
	}
	
	void set_symbol(char sym)
	{
		symbol = sym;
	}
	
	public void print()
	{
		System.out.println("< " + number + "  ,  " + symbol + " >");
	}
}


public class LZ
{
	private static ArrayList<String> Decomp_Dic = new ArrayList<String>();
	private static ArrayList<String> Comp_Dic = new ArrayList<String>();
	private static ArrayList<Integer> P_tags = new ArrayList<Integer>();
	private static ArrayList<String> S_tags = new ArrayList<String>();
	private JFrame frame1;
	private JFrame frame2;
	private JFrame frame3;
	
	private LZ()
	{
		initialize_Form1();
		initialize_Form2();
		initialize_Form3();
	}
	
	public static void main(String[] args)
	{
		BufferedReader br = null;                // br object from class to read from file
		BufferedWriter bw = null;               // bw object from class to write in file
		BufferedReader br_tag = null;
		
		try
		{
			File file = new File("Symbols.txt");
			File F_tag = new File("Tags.txt");
			if(!file.exists())
			{
				file.createNewFile();
			}
			if(!F_tag.exists())
			{
				F_tag.createNewFile();
			}
			
			br = new BufferedReader(new FileReader("Symbols.txt"));
			bw = new BufferedWriter(new FileWriter("Tags.txt"));
			br_tag = new BufferedReader(new FileReader("Tags.txt"));
			
			String line;
			Comp_Dic.add(null);
			
			boolean isLast = false;
			String str = null;
			while((line = br.readLine()) != null)
			{
				System.out.println("File Symbols : " + line);
				Compression(line, isLast);
			}
			
			System.out.println("Tags : ");
			for(int i = 0; i < P_tags.size(); i++)
			{
				System.out.println("< " + P_tags.get(i) + " , " + S_tags.get(i) + " >");
				bw.write("< " + P_tags.get(i) + " , " + S_tags.get(i) + " >");
				bw.newLine();
			}
			
			//// Decompression
			DeCompression(str, isLast);
			
			br.close();
			bw.close();
			br_tag.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					LZ window = new LZ();
					window.frame1.setVisible(true);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	private static void Compression(String line, boolean isLast)
	{
		String str;
		str = line.charAt(0) + "";
		boolean IsExist;
		for(int i = 1; i <= line.length(); i++)
		{
			IsExist = Comp_Dic.contains(str);
			if(!IsExist)
			{
				Comp_Dic.add(str);
			}
			else
			{
				while(Comp_Dic.contains(str) && i != line.length())
				{
					str += line.charAt(i) + "";
					i++;
				}
				if(i == line.length() && Comp_Dic.contains(str))
				{
					isLast = true;
					break;
				}
				Comp_Dic.add(str);
			}
			if(!(i == line.length()))
			{
				str = line.charAt(i) + "";
			}
		}
		
		
		System.out.println("Dictionary : ");
		for(int i = 0; i < Comp_Dic.size(); i++)
			System.out.println(i + "     " + Comp_Dic.get(i));
		
		ArrayList<LZData> Tags = new ArrayList<LZData>();
		LZData c = new LZData();
		
		for(int i = 1; i < Comp_Dic.size(); i++)
		{
			if(Comp_Dic.get(i).length() == 1)
			{
				c.set_number(0);
				P_tags.add(0);
				S_tags.add(Comp_Dic.get(i).charAt(0) + "");
				c.set_symbol(Comp_Dic.get(i).charAt(0));
				Tags.add(c);
			}
			else
			{
				for(int j = 1; j < i; j++)
				{
					if(Comp_Dic.get(j).contains(Comp_Dic.get(i).substring(0, Comp_Dic.get(i).length() - 1)))
					{
						P_tags.add(j);
						c.set_number(j);
						break;
					}
				}
				S_tags.add(Comp_Dic.get(i).charAt(Comp_Dic.get(i).length() - 1) + "");
				c.set_symbol(Comp_Dic.get(i).charAt(Comp_Dic.get(i).length() - 1));
				Tags.add(c);
			}
		}
		if(isLast)
		{
			for(int j = 1; j < Comp_Dic.size(); j++)
			{
				if(Comp_Dic.get(j).equals(str))
				{
					P_tags.add(j);
					S_tags.add(null);
					c.set_number(j);
					c.set_symbol('\0');
					Tags.add(c);
					break;
				}
			}
		}
	}
	
	private static void DeCompression(String str, boolean IsLast)
	{
		Decomp_Dic.add(null);
		for(int i = 0; i < P_tags.size(); i++)
		{
			String Get_str;
			if(P_tags.get(i) == 0)
			{
				Decomp_Dic.add(S_tags.get(i));
			}
			else
			{
				if(S_tags.get(i) != null)
				{
					Get_str = S_tags.get(P_tags.get(i) - 1) + S_tags.get(i);
					Decomp_Dic.add(Get_str);
				}
			}
		}
		if(IsLast)
		{
			Decomp_Dic.add(str);
		}
		System.out.println("Decompression text : ");
		for(int i = 1; i < Decomp_Dic.size(); i++)
			System.out.print(Decomp_Dic.get(i));
		
	}
	
	private void initialize_Form1()
	{
		frame1 = new JFrame();
		frame1.setBounds(100, 100, 600, 600);
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame1.getContentPane().setLayout(null);
		
		JLabel lblWel = new JLabel("Welcome ^^");
		lblWel.setBounds(10, 10, 100, 50);
		frame1.getContentPane().add(lblWel);
		
		JLabel lblName = new JLabel("Click on Your Choice.");
		lblName.setBounds(65, 100, 200, 50);
		frame1.getContentPane().add(lblName);
		
		JButton btnSubmit1 = new JButton("Compression");
		btnSubmit1.setBackground(Color.WHITE);
		btnSubmit1.setForeground(Color.RED);
		btnSubmit1.setBounds(65, 170, 150, 40);
		frame1.getContentPane().add(btnSubmit1);
		
		JButton btnSubmit2 = new JButton("De-compression");
		btnSubmit2.setBackground(Color.WHITE);
		btnSubmit2.setForeground(Color.RED);
		btnSubmit2.setBounds(350, 170, 150, 40);
		frame1.getContentPane().add(btnSubmit2);
		
		btnSubmit1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame1.setVisible(false);
				frame2.setVisible(true);
			}
		});
		btnSubmit2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame1.setVisible(false);
				frame3.setVisible(true);
			}
		});
	}
	
	private void initialize_Form2()
	{
		frame2 = new JFrame();
		frame2.setBounds(100, 100, 600, 600);
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame2.getContentPane().setLayout(null);
		
		
		JLabel lblWel = new JLabel("Compression Selected");
		lblWel.setBounds(90, 1, 180, 20);
		frame2.getContentPane().add(lblWel);
		
		JLabel lblDic = new JLabel("Get Your Data : ");
		lblDic.setBounds(5, 50, 100, 20);
		frame2.getContentPane().add(lblDic);
		
		JButton btnSubmit = new JButton("Get Data");
		btnSubmit.setBackground(Color.WHITE);
		btnSubmit.setForeground(Color.BLUE);
		btnSubmit.setBounds(180, 50, 100, 30);
		frame2.getContentPane().add(btnSubmit);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(180, 100, 250, 80);
		frame2.getContentPane().add(textArea_1);
		textArea_1.setColumns(20);
		
		JButton btnSubmit1 = new JButton("Compress");
		btnSubmit1.setBackground(Color.WHITE);
		btnSubmit1.setForeground(Color.BLUE);
		btnSubmit1.setBounds(180, 200, 100, 30);
		frame2.getContentPane().add(btnSubmit1);
		
		JLabel lblTags = new JLabel("Tags:");
		lblTags.setBounds(5, 250, 50, 60);
		frame2.getContentPane().add(lblTags);
		
		JTextField textField_1 = new JTextField();
		textField_1.setBounds(180, 270, 200, 250);
		frame2.getContentPane().add(textField_1);
		textField_1.setColumns(20);
		
		btnSubmit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				String FileName = f.getAbsolutePath();
				try
				{
					BufferedReader br = new BufferedReader(new FileReader("Symbols.txt"));
					textArea_1.read(br, null);
					br.close();
					textArea_1.requestFocus();
				}
				catch(Exception e1)
				{
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		btnSubmit1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					for(int i = 0; i < P_tags.size(); i++)
					{
						BufferedReader br = null;
						br = new BufferedReader(new FileReader("Tags.txt"));
						textField_1.read(br, null);
						br.close();
						textField_1.requestFocus();
					}
				}
				catch(Exception e1)
				{
					JOptionPane.showMessageDialog(null, e1);
					e1.printStackTrace();
				}
			}
		});
		
		JButton btnSubmit3 = new JButton("UP");
		btnSubmit3.setBackground(Color.WHITE);
		btnSubmit3.setForeground(Color.BLACK);
		btnSubmit3.setBounds(1, 1, 70, 20);
		frame2.getContentPane().add(btnSubmit3);
		
		
		btnSubmit3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame2.setVisible(false);
				frame1.setVisible(true);
			}
		});
		
	}
	
	private void initialize_Form3()
	{
		frame3 = new JFrame();
		frame3.setBounds(100, 100, 600, 600);
		frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame3.getContentPane().setLayout(null);
		
		JLabel lblWel = new JLabel("De-compression Selected");
		lblWel.setBounds(90, 1, 150, 20);
		frame3.getContentPane().add(lblWel);
		
		JLabel lblget = new JLabel("Get your Tags : ");
		lblget.setBounds(1, 50, 150, 20);
		frame3.getContentPane().add(lblget);
		
		JButton btnSubmit = new JButton("Get Tags");
		btnSubmit.setBackground(Color.WHITE);
		btnSubmit.setForeground(Color.BLUE);
		btnSubmit.setBounds(180, 50, 150, 30);
		frame3.getContentPane().add(btnSubmit);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(200, 100, 250, 150);
		frame3.getContentPane().add(textArea_1);
		textArea_1.setColumns(20);
		
		JLabel lblDecomp = new JLabel("De-compress For previous Tags:");
		lblDecomp.setBounds(1, 350, 250, 50);
		frame3.getContentPane().add(lblDecomp);
		
		JButton btnSubmit1 = new JButton("Decompress");
		btnSubmit1.setBackground(Color.WHITE);
		btnSubmit1.setForeground(Color.BLUE);
		btnSubmit1.setBounds(180, 300, 150, 30);
		frame3.getContentPane().add(btnSubmit1);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setBounds(200, 350, 250, 120);
		frame3.getContentPane().add(textArea_2);
		textArea_2.setColumns(20);
		//for(int i=0; i<Decomp_Dic.size(); i++)
		//textArea_2.append(Decomp_Dic.get(i));
		
		btnSubmit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				String FileName = f.getAbsolutePath();
				try
				{
					BufferedReader br = new BufferedReader(new FileReader("Tags.txt"));
					textArea_1.read(br, null);
					br.close();
					textArea_1.requestFocus();
				}
				catch(Exception e1)
				{
					JOptionPane.showMessageDialog(null, e);
				}
			}
		});
		
		btnSubmit1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for(int i = 0; i < Decomp_Dic.size(); i++)
					textArea_2.append(Decomp_Dic.get(i));
			}
		});
		
		
		JButton btnSubmit3 = new JButton("UP");
		btnSubmit3.setBackground(Color.WHITE);
		btnSubmit3.setForeground(Color.BLACK);
		btnSubmit3.setBounds(1, 1, 70, 20);
		frame3.getContentPane().add(btnSubmit3);
		
		btnSubmit3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				frame3.setVisible(false);
				frame1.setVisible(true);
			}
		});
	}
}
