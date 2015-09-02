package ch.epfl.mobots;
import java.util.List;
import org.freedesktop.DBus.Method.NoReply;
import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.UInt16;

public interface AsebaNetwork extends DBusInterface
{

  public void LoadScripts(String fileName);
  public List<String> GetNodesList();
  public short GetNodeId(String node);
  public List<String> GetVariablesList(String node);
  public void SetVariable(String node, String variable, List<Short> data);
  public List<Short> GetVariable(String node, String variable);
  public void SendEvent(UInt16 event, List<Short> data);
  public void SendEventName(String name, List<Short> data);
  public DBusInterface CreateEventFilter();
}
