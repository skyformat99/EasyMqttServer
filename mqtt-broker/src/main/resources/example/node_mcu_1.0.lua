-- init mqtt client with keepalive timer 120sec
m = mqtt.Client("clientid", 120, "user", "password")

-- setup Last Will and Testament (optional)
-- Broker will publish a message with qos = 0, retain = 0, data = "offline"
-- to topic "/lwt" if client don't send keepalive packet
m:lwt("/lwt", "offline", 0, 0)

m:on("connect", function(con) print ("connected") end)
m:on("offline", function(con) print ("offline") end)

-- on publish message receive event
m:on("message", function(conn, topic, data)
  print(topic .. ":" )
  if data ~= nil then
    print(data)
  end
end)

-- for secure: m:connect("192.168.16.9", 1880, 1)
m:connect("192.168.16.9", 1883, 0, function(conn) print("connected") end)

-- subscribe topic with qos = 0
m:subscribe("/topic",0, function(conn) print("subscribe success") end)

-- publish a message with data = hello, QoS = 0, retain = 0
m:publish("/topic","hello",0,0, function(conn) print("sent") end)

m:close();
-- you can call m:connect again



m = mqtt.Client("clientid", 120, "user", "password");

m:lwt("/lwt", "offline", 0, 0);

m:on("message", function(conn, topic, data) print(topic .. ":" )  if data ~= nil then  print(data) end end);
m:connect("192.168.16.9", 1883, 0, function(conn) print("connected") end);

m:connect("impkd.com", 1883, 0, function(conn) print("connected") end);

m:subscribe("/topic",0, function(conn) print("subscribe success") end);

m:publish("/topic","hello",0,0, function(conn) print("sent") end);