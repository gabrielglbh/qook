interface TitleParams {
  title: string;
}

export default function Title ({title}: TitleParams) {
    return (
        <div className='text-5xl font-bold mb-8 text-center'>
            {title}  
        </div>
    );
}
