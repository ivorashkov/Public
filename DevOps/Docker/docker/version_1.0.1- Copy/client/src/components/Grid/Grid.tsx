import { FC } from 'react';
import type { GridItems } from '../../types';
import { Offer } from '../Offer';
import { GridItem } from './GridItem';

export const Grid: FC<GridItems> = ({offers}) => {
    return (
        <div className="grid">
            <div className="grid__row">
                {offers.map((item)=> 
                    <GridItem key={Date.now()}>
                        <Offer title={item.title} description={item.description}/>
                    </GridItem>)}
            </div>
        </div>
    );
}