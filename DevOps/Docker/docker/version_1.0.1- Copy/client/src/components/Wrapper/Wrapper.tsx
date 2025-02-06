import React, { FC } from 'react';
import type { Child } from '../../types';

export const Wrapper: FC<Child> = ({children}) => {
    return (
        <div className="wrapper">
            {children}
        </div>
    );
}
