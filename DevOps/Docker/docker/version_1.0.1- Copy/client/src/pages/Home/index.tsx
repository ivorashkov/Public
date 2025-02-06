import { Grid } from "../../components/Grid";
import { Section } from "../../components/Section";
import { Shell } from "../../components/Shell";

const dummyObj = 
[
        {
            title: 'title 1',
            description: 'ala bala 1'
        },
        {
            title: 'title 2',
            description: 'ala bala 2'
        },
        {
            title: 'title 3',
            description: 'ala bala 3'
        },
        {
            title: 'title 4',
            description: 'ala bala 4'
        },
        {
            title: 'title 5',
            description: 'ala bala 5'
        },
        {
            title: 'title 6',
            description: 'ala bala 6'
        }
];

export const Home = () => {
    return (
        <>
            <Section>
                <Shell>
                    <Grid offers={dummyObj}/>
                </Shell>
            </Section>
        </>
    );
}