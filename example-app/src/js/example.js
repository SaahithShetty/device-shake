import { DeviceShake } from 'device-shake';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    DeviceShake.echo({ value: inputValue })
}
