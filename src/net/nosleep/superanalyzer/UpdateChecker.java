package net.nosleep.superanalyzer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.nosleep.superanalyzer.util.Constants;
import net.nosleep.superanalyzer.util.Misc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateChecker
{

	JFrame _window;

	public UpdateChecker(JFrame window)
	{
		_window = window;

		CheckerThread t = new CheckerThread();
		t.start();
	}

	private class CheckerThread extends Thread
	{
		public void run()
		{
			check();
		}
	}

	private void check()
	{
		double availableVersion = 0.0;

		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse("http://www.nosleep.net/download/version_sa.xml");
			doc.getDocumentElement().normalize();

			NodeList nodeLst = doc.getElementsByTagName("superanalyzer");

			for (int i = 0; i < nodeLst.getLength(); i++)
			{
				Node fstNode = nodeLst.item(i);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE)
				{
					Element fstElement = (Element) fstNode;
					NodeList fstElementNodeList = fstElement.getElementsByTagName("version");
					Element versionElement = (Element) fstElementNodeList.item(0);

					NodeList versionElementNodes = versionElement.getChildNodes();

					String value = ((Node) versionElementNodes.item(0)).getNodeValue();

					availableVersion = Double.parseDouble(value);
				}
			}
		}
		catch (Exception e)
		{
		}

		if (availableVersion > Double.parseDouble(Constants.VERSION))
		{
			JOptionPane.showMessageDialog(_window, Misc.getString("UPDATE_AVAILABLE"), "Super Analyzer",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
}
