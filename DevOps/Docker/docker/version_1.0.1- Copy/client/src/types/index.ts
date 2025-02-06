export type Child = {
    children: JSX.Element;
}

export type GridItems = {
    offers: Grid[];
};

export interface Grid {
    title: string;
    description: string;
}
