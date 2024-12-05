import React from 'react';
import './ShopItemComponent.css';

type Props = {
  name: string;
  imageUrl: string;
  price: number;
  onClick: () => void;
}

const ShopItemComponent: React.FC<Props> = ({name, imageUrl, price, onClick}) => {
return (
    <div className="shop-item">
        <img src={imageUrl} alt={name} className="shop-item-image"/>
        <div className="shop-item-details">
            <p className="shop-item-name">{name}</p>
            <p className="shop-item-price">{price + " pts"}</p>
        </div>
        <button type="button" className="shop-item-button" onClick={onClick}>purchase</button>
    </div>
    );
};

export default ShopItemComponent;