
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;

class TradeActionsPanel extends JPanel implements ActionListener{

    JComboBox yourStuff;
	JList offeredStuff;
	JButton accept;
	JButton decline;
	ClientJApplet client;
	boolean updated;
	ArrayList<String> list1;

    public TradeActionsPanel(ClientJApplet c) 
    {
    	super(new BorderLayout());
    	client=c;
    	list1=new ArrayList<>();
    	list1.add("You Offer:");
    	String[] list=new String[25];
    	list[0]="You Offer:";
    	offeredStuff=new JList(list);
    	MouseListener mouseListener = new MouseAdapter() 
    	{
		     public void mouseClicked(MouseEvent e) 
		     {
		         int index = offeredStuff.locationToIndex(e.getPoint());
		         if(index!=0)
		         {
		         	yourStuff.addItem(list1.get(index));
		         	client.sendMessage("unoffer "+list1.get(index));
		         	list1.remove(index);
		         	String[] list2=new String[25];
			    	for(int i=0;i<list1.size();i++)
			    	{
			    		list2[i]=list1.get(i);
			    	}
			    	
			    	offeredStuff.setListData(list2);
		         }
		     }
		 };
 		offeredStuff.addMouseListener(mouseListener);
    	
    	updated=false;
    	
    	accept=new JButton("Accept Trade");
    	decline=new JButton("Decline Trade");
    	
    	accept.setActionCommand("accept");
    	decline.setActionCommand("decline");
    	decline.setEnabled(false);
    	accept.addActionListener(this);
    	decline.addActionListener(this);
    	
    	this.add(accept, BorderLayout.NORTH);
    	this.add(decline, BorderLayout.SOUTH);
    	
    	this.add(offeredStuff, BorderLayout.WEST);
    }
    public void setMyStuff()
    {
    	if(updated)
    	{
    		
    	}
    	else
    	{
    		String[] arr=client.getInven().getMycrap();
    		yourStuff=new JComboBox(arr);
    		yourStuff.addActionListener(this);
    		this.add(yourStuff, BorderLayout.EAST);
    		updated=true;
    	}
    	
    }
    public void actionPerformed(ActionEvent e) 
    {
    	if ("accept".equals(e.getActionCommand())) 
    	{
    		client.sendMessage("acceptTrade");
    		accept.setEnabled(false);
    		decline.setEnabled(true);
    	}
    	else if("decline".equals(e.getActionCommand()))
    	{
    		client.sendMessage("unacceptTrade");
    		accept.setEnabled(true);
    		decline.setEnabled(false);
    	}
    	else
    	{
    		JComboBox cb = (JComboBox)e.getSource();
	        String offerName = (String)cb.getSelectedItem();
	        updateOffer(offerName);
    	}
        
    }
    
    public void updateOffer(String name)
    {
    	list1.add(name);
    	String[] list=new String[25];
    	for(int i=0;i<list1.size();i++)
    	{
    		list[i]=list1.get(i);
    	}
    	
    	offeredStuff.setListData(list);
    	yourStuff.removeItem(name);
    	client.sendMessage("offer "+name);
    }
    
}
