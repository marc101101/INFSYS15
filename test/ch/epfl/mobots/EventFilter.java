package ch.epfl.mobots;

import java.util.List;

import org.freedesktop.DBus.Method.NoReply;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.UInt16;
import org.freedesktop.dbus.DBusSignal;

public interface EventFilter extends DBusInterface
{

  public void ListenEvent(UInt16 eventId);
  public void ListenEventName(String eventName);
  public void IgnoreEvent(UInt16 eventId);
  public void IgnoreEventName(String eventName);
  public DBusSignal Event(UInt16 id, String name, List<Short> payloadData);
}
