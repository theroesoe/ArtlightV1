package com.example.felix.artlightv1;

import android.os.AsyncTask;
import android.util.Log;

import java.net.SocketException;
        import java.util.List;
        import java.net.InetAddress;

        import artnet4j.ArtNet;
        import artnet4j.ArtNetException;
        import artnet4j.ArtNetNode;
        import artnet4j.events.ArtNetDiscoveryListener;
        import artnet4j.packets.ArtDmxPacket;

public class ArtNetClient {
    private int sequenceId;
    private ArtNet artnet;
    private ArtNetNode receiver;

    public ArtNetClient()
    {
        artnet = new ArtNet();
        Log.d("Fehler", artnet.toString());
    }

    public int getSequenceId(){
        return sequenceId;
    }


    public void open()
    {
        open(null, null);
    }

    public void open(InetAddress in, String address)
    {
        try
        {
            // sender
            artnet.start(in);
            setReceiver(address);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
        catch (ArtNetException e) {
            e.printStackTrace();
        }
    }

    public void setReceiver(String address)
    {
        if (null == address)
            receiver = null;

        try
        {
            receiver = new ArtNetNode();
            receiver.setIPAddress(InetAddress.getByName(address));
            Log.d("Fehler", "receiver festgelegt");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close()
    {
        artnet.stop();
    }

    public void send(int universe, byte[] data)
    {
        Log.d("Fehler", "send geöffnen ");
        send(receiver, universe, data);
    }

    public void send(ArtNetNode node, int universe, byte[] data)
    {
        ArtDmxPacket dmx = new ArtDmxPacket();

        dmx.setUniverse(0, universe);
        dmx.setSequenceID(sequenceId % 256);
        dmx.setDMX(data, data.length);

        if (receiver != null)
            artnet.unicastPacket(dmx, node);
        else
            artnet.broadcastPacket(dmx);

        sequenceId++;
    }
}