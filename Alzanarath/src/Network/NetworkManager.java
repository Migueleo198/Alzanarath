package Network;


import java.io.*;
import java.net.*;
import java.util.*;

public class NetworkManager {
 // Shared network constants
 public static final int DEFAULT_PORT = 55555;
 public static final String DEFAULT_HOST = "localhost";
 
 // Message types
 public static final String PLAYER_JOIN = "JOIN";
 public static final String PLAYER_UPDATE = "UPDATE";
 public static final String PLAYER_LEAVE = "LEAVE";
 public static final String WORLD_STATE = "WORLD";
}


