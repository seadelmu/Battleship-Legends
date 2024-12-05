import React, {useState} from 'react';
import ShopItemComponent from "./ShopItemComponent/ShopItemComponent";
import './ShopComponent.css';
import {FC} from 'react';
// @ts-ignore
import {useWebSocket} from "../WebsocketContextProvider.jsx";

interface Props {
    points: number;
    handleClose: () => void;
}

function getCookie(name: string): string | null {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop()?.split(';').shift() || null;
    return null;
}

function getImagePath(imageName:string, imagePath:string="../public/item_icons/"): string{
    return imagePath + imageName;
}

const ShopComponent: FC<Props> = ({points, handleClose}) => {
    const {sockets} = useWebSocket();
    const socket = sockets[getCookie("lobbyCode")];

    const image_path = "../public/item_icons/"
  const items = [
        {name: "Sonar", imageUrl: getImagePath("BSL-Sonar.png"), price: 5},
        {name: "Bomb", imageUrl: getImagePath("BSL-Bomb.png"), price: 20},
        {name: "Nuke", imageUrl: getImagePath("BSL-Nuke.png"), price: 50 },
        {name: "Decoy", imageUrl: getImagePath("BSL-Decoy.png"), price: 5},
  ];

    const handlePurchase = (item_name: string, item_price: number) => {
        if (points < item_price) {
            console.log("Not enough points to buy " + item_name);
            return;
        }
        console.log(item_name + " purchased for " + item_price + " points");

        // Emit a socket event to notify the server about the purchase
        const message = JSON.stringify({
            type: 'PURCHASE_POWERUP',
            item_name,
            item_price,
            sessionId: getCookie("sessionId")
        });
        socket.send(message);
    }

  return (
    <div className="shop-container">
        <div className = "shop-top-bar">
            <div className="points-container">
                Points: {points}
            </div>
            <button type="button" className="shop-close-button" onClick={handleClose}>x</button>
        </div>
        <div className="shop-items-container">
            {items.map((item, index) => (
                <ShopItemComponent key={index} name={item.name} imageUrl={item.imageUrl} price={item.price} onClick={() => {
                    handlePurchase(item.name, item.price);
                }} />
            ))}
        </div>
    </div>
  );
};

export default ShopComponent;