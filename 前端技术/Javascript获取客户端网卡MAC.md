# Javascript获取客户端网卡MAC

注：因为需要用到 ActiveXObject 仅针对IE浏览器有效

```javascript
function getClientMac(){
    try{
        var macAddress = "";
        var locator = new ActiveXObject("WbemScripting.SWbemLocator");
        var service = locator.ConnectServer(".");
        var properties = service.ExecQuery("SELECT * FROM Win32_NetworkAdapterConfiguration");
        var e = new Enumerator(properties);
        while(!e.atEnd()) {
            var p = e.item ();
            if(p!=null && p.IPAddress!=null){
                if(macAddress.length>0){
                    macAddress+="_";
                }
                   macAddress += p.MACAddress;
            }
            e.moveNext();
        }
        return macAddress;
    }catch(e){
        return "";
    }
}
```
